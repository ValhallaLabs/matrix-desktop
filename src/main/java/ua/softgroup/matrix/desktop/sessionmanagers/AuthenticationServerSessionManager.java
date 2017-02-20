package ua.softgroup.matrix.desktop.sessionmanagers;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.LoginLayoutController;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.server.desktop.api.Constants;
import ua.softgroup.matrix.server.desktop.api.ServerCommands;
import ua.softgroup.matrix.server.desktop.model.ClientSettingsModel;
import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.TokenModel;
import ua.softgroup.matrix.server.desktop.model.UserPassword;
import java.io.*;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static ua.softgroup.matrix.server.desktop.api.Constants.INVALID_PASSWORD;
import static ua.softgroup.matrix.server.desktop.api.Constants.INVALID_USERNAME;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class AuthenticationServerSessionManager extends ServerSessionManager {
    private LoginLayoutController loginLayoutController;
    private Emitter<UserPassword> userPasswordEmitter;
    private Disposable disposableSubscription;
    private CountDownLatch countDownLatch;

    public AuthenticationServerSessionManager(LoginLayoutController loginLayoutController) {
        this.loginLayoutController = loginLayoutController;
        countDownLatch = new CountDownLatch(1);
        disposableSubscription = Observable.using(this::openSocketConnection, this::createBindedObservable, this::closeSocketConnection)
                .subscribe(this::startMainLayoutAndDisposeManager, this::handleExceptions);
    }

    /**
     * The factory function to create an Observable, that executes process of authentication
     * and gets all projects of user.
     * @param socket The socket which is dependent to an observable
     * @return Observable
     */
    @Override
    protected Observable<Boolean> createBindedObservable(Socket socket) {
        return Observable.create(this::createObservableEmitter)
                .map(userPassword -> authenticateUser(userPassword, socket))
                .filter(this::handleServerAuthResponse)
                .map(this::composeTokenModel)
                .map(tokenModel -> getAllProjectsAndClientSettings(tokenModel,socket))
                .doOnNext(this::setProjectsModelsToCurrentSessionInfo)
                .map(this::setClientSettingToCurrentSessionInfo)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Function to create an custom Observable emitter.
     * countDownLatch.countDown() removes block of the main thread, and lets user to use userPasswordEmitter.
     * @param e observable emitter for user password models
     */
    private void createObservableEmitter(ObservableEmitter<UserPassword> e) {
        userPasswordEmitter = e;
        countDownLatch.countDown();
     }

    /**
     * Sends to server command about authentication and DTO with authentication user info.
     * @param userPassword DTO with username and password
     * @return String object that contains response about result of authentication
     */
    private String authenticateUser(UserPassword userPassword, Socket socket) throws IOException {
        objectOutputStream.writeObject(ServerCommands.AUTHENTICATE);
        objectOutputStream.writeObject(userPassword);
        objectOutputStream.flush();
        return new DataInputStream(socket.getInputStream()).readUTF();
    }

    /**
     * Filter that handles result of authentication, and continues chain in case of positive result and notify UI
     * to open main window, or notify user about incorrect authentication user info.
     * @param response String object that contains response about result of authentication
     * @return boolean result of authentication
     */
    private boolean handleServerAuthResponse(String response) {
        logger.debug("Auth response {}", response);
        if (Constants.INVALID_USERNAME.name().equals(response) || Constants.INVALID_PASSWORD.name().equals(response)) {
            loginLayoutController.errorLoginPassword();
            return false;
        } else {
            return true;
        }
    }

    /**
     * Creates TokenModel from received token in a string
     * @param token String object that contains token
     * @return tokenModel TokenModel with current token
     */
    private TokenModel composeTokenModel(String token) {
        TokenModel tokenModel = new TokenModel(token);
        CurrentSessionInfo.setTokenModel(tokenModel);
        return tokenModel;
    }

    /**
     * Sends to server command about getting all user active projects and DTO with session token.
     * @param tokenModel DTO with token
     * @param socket socket for opening object input stream
     * @return object that has to contain set of project models
     */
    private InputStream getAllProjectsAndClientSettings(TokenModel tokenModel, Socket socket) throws IOException {
        objectOutputStream.writeObject(ServerCommands.GET_ALL_PROJECT);
        objectOutputStream.writeObject(tokenModel);
        objectOutputStream.writeObject(ServerCommands.UPDATE_SETTING);
        objectOutputStream.flush();
        return socket.getInputStream();
    }

    /**
     * Tries to receive set of project models, and then set it to CurrentSessionInfo.
     * @param inputStream for open ObjectInputStream
     * @return inputStream for a further action
     */
    private InputStream setProjectsModelsToCurrentSessionInfo(InputStream inputStream) throws IOException {
        Set<ProjectModel> projectModels = null;
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            projectModels = (Set<ProjectModel>) objectInputStream.readObject();
            logger.debug("Project models received successfully: {}", projectModels);
        } catch (ClassNotFoundException e) {
            logger.debug("Project models received unsuccessfully", e);
        }
        CurrentSessionInfo.setUserActiveProjects(projectModels);
        return inputStream;
    }

    /**
     * Tries to receive client settings model, and then set it to CurrentSessionInfo.
     * @param inputStream for open ObjectInputStream
     * @return settingsResult result of getting client settings
     */
    private Boolean setClientSettingToCurrentSessionInfo(InputStream inputStream) throws IOException {
        // TODO try with resources
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        ClientSettingsModel clientSettingsModel = null;
        try {
            clientSettingsModel = (ClientSettingsModel) objectInputStream.readObject();
            CurrentSessionInfo.setClientSettingsModel(clientSettingsModel);
            logger.debug("Client settings model: {}", clientSettingsModel);
            return true;
        } catch (ClassNotFoundException e) {
            logger.debug("Client settings model wasn't received: {}", e);
            return false;
        }
    }

    /**
     * Tells {@link LoginLayoutController} to open main window and dispose current session.
     * @param authSessionStatus useless param at the moment
     */
    private void startMainLayoutAndDisposeManager(Boolean authSessionStatus){
        if(authSessionStatus) {
            logger.debug("Authentication completed");
            loginLayoutController.closeLoginLayoutAndStartMainLayout();
            disposableSubscription.dispose();
        } else {
            logger.debug("Authentication wasn't complete successfully");
            disposableSubscription.dispose();
            // TODO: tell user to try to restart client or login again.
        }
    }

    /**
     * Handles exception which may be thrown while session is executing.
     * @param throwable object that has to cast to throwable
     */
    private void handleExceptions(Throwable throwable) {
        //TODO: Tell UI to show message, that client is unable to start, restart it
        logger.debug("Unable to start client: ", throwable);
    }

    /**
     * Received user data for authentication, creates UserPassword DTO, end emit it into observable.
     * countDownLatch.await() blocks current thread, until userPasswordEmitter will be initialized.
     * @param userNameString string that contains user name
     * @param userPasswordString string that contains user password
     */
    public void sendUserAuthData(String userNameString, String userPasswordString) {
        UserPassword userPassword = new UserPassword(userNameString, userPasswordString);
        try {
            if (userPasswordEmitter ==  null) {
                countDownLatch.await();
            }
            userPasswordEmitter.onNext(userPassword);
        } catch (InterruptedException e) {
            logger.debug("Something went wrong with sending user auth data to server: ", e);
            //TODO: Tell UI to show message, that client is unable to start, restart it
        }
    }

    /**
     * Closes current authentication session
     */
    @Override
    public void closeSession(){
        if(!disposableSubscription.isDisposed()) {
            disposableSubscription.dispose();
        }
    }
}
