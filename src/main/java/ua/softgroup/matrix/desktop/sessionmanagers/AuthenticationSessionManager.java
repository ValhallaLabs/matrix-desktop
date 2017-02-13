package ua.softgroup.matrix.desktop.sessionmanagers;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.utils.SocketProvider;
import ua.softgroup.matrix.server.desktop.api.Constants;
import ua.softgroup.matrix.server.desktop.api.ServerCommands;
import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.TokenModel;
import ua.softgroup.matrix.server.desktop.model.UserPassword;
import java.io.*;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class AuthenticationSessionManager {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationSessionManager.class);
    private Emitter<UserPassword> userPasswordEmitter;
    private ObjectOutputStream objectOutputStream;
    private DataInputStream dataInputStream;
    private Disposable disposableSubscription;
    private CountDownLatch countDownLatch;

    /**
     * Received user data for authentication, creates UserPassword DTO, end emit it into observable.
     * countDownLatch.await() blocks current thread, until userPasswordEmitter will be initialized.
     * @param userNameString string that contains user name
     * @param userPasswordString string that contains user password
     */
    public void sendUserAuthData(String userNameString, String userPasswordString) {
        UserPassword userPassword = new UserPassword(userNameString, userPasswordString);
        try {
            countDownLatch.await();
            userPasswordEmitter.onNext(userPassword);
        } catch (InterruptedException e) {
            logger.debug("sendUserAuthData. Something went wrong {}", e.toString());
            //TODO: Some kind of global crash. Restart app.
        }
    }

    public AuthenticationSessionManager() {
        countDownLatch = new CountDownLatch(1);
        disposableSubscription = Observable.using(this::openSocketConnection, this::createObservable, this::closeSocketConnection)
                .subscribe(this::setProjectsModelsToCurrentSessionInfo, this::handleExceptions);
    }

    /**
     * The factory function to create a resource object that depends on the Observable.
     * Creates a new socket connection with server.
     * @return a new socket connection
     */
    private Socket openSocketConnection() throws IOException {
        Socket socket = SocketProvider.openNewConnection();
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
        logger.debug("Open socket connection");
        return socket;
    }

    /**
     * The function that will dispose of the resource, that depends to the Observable.
     * Sends command to server about closing connection and closes socket connection.
     * @param socket The socket which is dependent to an observable
     */
    private void closeSocketConnection(Socket socket) throws IOException {
        logger.debug("Socket connection closed");
        objectOutputStream.writeObject(ServerCommands.CLOSE);
        objectOutputStream.flush();
        socket.close();
    }

    /**
     * The factory function to create an Observable, that executes process of authentication
     * and gets all projects of user.
     * @param socket The socket which is dependent to an observable
     * @return Observable
     */
    private Observable createObservable(Socket socket) {
        return Observable.create(this::createObservableEmitter)
                .map(this::authenticateUser)
                .filter(this::handleServerAuthResponse)
                .map(this::composeTokenModel)
                .map(tokenModel -> getAllProjects(tokenModel, socket))
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
    private String authenticateUser(UserPassword userPassword) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject(ServerCommands.AUTHENTICATE);
        objectOutputStream.writeObject(userPassword);
        objectOutputStream.flush();
        return dataInputStream.readUTF();
    }

    /**
     * Filter that handles result of authentication, and continues chain in case of positive result,
     * or notify user about incorrect authentication user info.
     * @param response String object that contains response about result of authentication
     * @return boolean result of authentication
     */
    private Boolean handleServerAuthResponse(String response) {
        logger.debug("Auth response {}", response);
        Boolean tokenValidationResult = !Constants.INVALID_USERNAME.name().equals(response) &&
                !Constants.INVALID_PASSWORD.name().equals(response);
        if(!tokenValidationResult) {
            //TODO: Notify user about wrong login data
            synchronized (this){
                this.notify();
            }
        }
        return tokenValidationResult;
    }

    /**
     * Creates TokenModel from received token in a string
     * @param token String object that contains token
     * @return tokenModel TokenModel with current token
     */
    private TokenModel composeTokenModel(String token) {
        TokenModel tokenModel = new TokenModel();
        tokenModel.setToken(token);
        return tokenModel;
    }

    /**
     * Sends to server command about getting all user active projects and DTO with session token.
     * @param tokenModel DTO with token
     * @param socket socket for opening object input stream
     * @return object that has to contain set of project models
     */
    private Object getAllProjects(TokenModel tokenModel, Socket socket) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject(ServerCommands.GET_ALL_PROJECT);
        objectOutputStream.writeObject(tokenModel);
        objectOutputStream.flush();
        return new ObjectInputStream(socket.getInputStream()).readObject();
    }

    /**
     * Tries to set received object response into set of project models, and then set it to CurrentSessionInfo.
     * Dispose authentication session.
     * @param objectResponse object that has to contain set of project models
     */
    private void setProjectsModelsToCurrentSessionInfo(Object objectResponse){
        //TODO: provide multithreading on CurrentSessionInfo
        Set<ProjectModel> projectModels = null;
        try {
            projectModels = (Set<ProjectModel>) objectResponse;
            logger.debug("Project models {}", projectModels.toString());
        } catch (Exception e) {
            logger.debug("Server doesn't return any projects");
        }
        CurrentSessionInfo.setUserActiveProjects(projectModels);
        disposableSubscription.dispose();
    }

    /**
     * Handles exception which may be thrown while session is executing.
     * @param throwable object that has to cast to throwable
     */
    private void handleExceptions(Object throwable) {
        //TODO: Think of what to do if exception is thrown
        ((Throwable) throwable).printStackTrace();
        disposableSubscription.dispose();
    }

    //TODO: Temporary method, delete before merging
    public static void main(String[] args) {
        AuthenticationSessionManager authenticationSessionManager = new AuthenticationSessionManager();
        authenticationSessionManager.sendUserAuthData("sup", "asdf");
    }
}
