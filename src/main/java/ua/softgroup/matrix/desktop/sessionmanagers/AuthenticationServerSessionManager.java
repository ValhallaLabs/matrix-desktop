package ua.softgroup.matrix.desktop.sessionmanagers;

import ua.softgroup.matrix.api.ServerCommands;
import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import ua.softgroup.matrix.api.model.datamodels.AuthModel;
import ua.softgroup.matrix.api.model.datamodels.InitializeModel;
import ua.softgroup.matrix.api.model.requestmodels.RequestModel;
import ua.softgroup.matrix.api.model.responsemodels.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.LoginLayoutController;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.utils.CommandExecutioner;
import ua.softgroup.matrix.desktop.utils.SocketProvider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import static ua.softgroup.matrix.api.model.responsemodels.ResponseStatus.SUCCESS;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class AuthenticationServerSessionManager {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServerSessionManager.class);
    private LoginLayoutController loginLayoutController;
    private Emitter<AuthModel> authModelEmitter;
    private Disposable socketDisposable;
    private CountDownLatch countDownLatch;
    private CommandExecutioner commandExecutioner;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;



    public AuthenticationServerSessionManager(LoginLayoutController loginLayoutController) {
        this.loginLayoutController = loginLayoutController;
        countDownLatch = new CountDownLatch(1);
        socketDisposable = Observable.using(this::openSocketConnection, this::createBindedObservable, this::closeSocketConnection)
                .subscribeOn(Schedulers.io())
                .subscribe(this::finishAuthentication, this::handleExceptions);
    }

    /**
     * The factory function to create a resource object that depends on the Observable.
     * Creates a new socket connection with server and commandExecutioner.
     * @return a new socket connection
     */
    private Socket openSocketConnection() throws IOException {
        Socket socket = SocketProvider.openNewConnection();
        commandExecutioner = new CommandExecutioner();
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        logger.debug("Connection is opened");
        return socket;
    }

    /**
     * The factory function to create an Observable, that executes process of authentication
     * and gets all projects of user.
     * @param socket The socket which is dependent to an observable
     * @return Observable
     */
    private Observable<ResponseModel<InitializeModel>> createBindedObservable(Socket socket) {
        return Observable.create(this::createObservableEmitter)
                .map(authModel -> authenticateUser(authModel, socket))
                .filter(this::handleServerAuthResponse);
    }

    /**
     * The function that will dispose of the resource, that depends to the Observable.
     * Sends command to server about closing connection and closes socket connection.
     * @param socket The socket which is dependent to an observable
     */
    private void closeSocketConnection(Socket socket) throws IOException {
        objectOutputStream.writeObject(ServerCommands.CLOSE);
        objectOutputStream.flush();
        socket.close();
        logger.debug("Connection is closed");
    }

    /**
     * Function to create an custom Observable emitter.
     * countDownLatch.countDown() removes block of the main thread, and lets user to use authModelEmitter.
     * @param e observable emitter for user password models
     */
    private void createObservableEmitter(ObservableEmitter<AuthModel> e) {
        authModelEmitter = e;
        countDownLatch.countDown();
     }

    /**
     * Sends to server command about authentication and DTO with authentication user info.
     * Tries to read {@link InitializeModel} object from {@link ObjectInputStream}.
     * @param authModel DTO with username and password
     * @return {@link InitializeModel} which may contains all start settings and info in case of success
     */
    private ResponseModel<InitializeModel> authenticateUser(AuthModel authModel, Socket socket) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject(ServerCommands.AUTHENTICATE);
        objectOutputStream.writeObject(new RequestModel(CurrentSessionInfo.getToken(), authModel));
        objectOutputStream.flush();

        ResponseModel<InitializeModel> responseModel = (ResponseModel<InitializeModel>) objectInputStream.readObject();
        logger.debug("Raw response: {}", responseModel.toString());
        return responseModel;
    }

    /**
     * Checks {@link InitializeModel} response status.
     * @param responseModel a {@link ResponseModel<InitializeModel>} object with response status and optional which may
     * contains all start settings and info in case of success.
     * @return boolean result of authentication
     */
    private boolean handleServerAuthResponse(ResponseModel<InitializeModel> responseModel) {
        if (SUCCESS != responseModel.getResponseStatus()) {
            loginLayoutController.errorLoginPassword();
            return false;
        }
        return true;
    }

    /**
     * Saves {@link InitializeModel} to {@link CurrentSessionInfo}.
     * Tells {@link LoginLayoutController} to open main window and dispose current session.
     * @param responseModel a {@link ResponseModel<InitializeModel>} object which contains all start settings and info.
     */
    private void finishAuthentication(ResponseModel<InitializeModel> responseModel){
        if(responseModel.getContainer().isPresent()) {
            CurrentSessionInfo.setInitializeModel(responseModel.getContainer().get());
            logger.debug("Authentication completed");
            socketDisposable.dispose();
            logger.debug("socketDisposable is disposed: {}", socketDisposable.isDisposed());
            loginLayoutController.closeLoginLayoutAndStartMainLayout();
            return;
        }
        handleExceptions(new Exception());
    }

    /**
     * Handles exception which may be thrown while session is executing.
     * @param throwable possible exception
     */
    private void handleExceptions(Throwable throwable) {
        logger.debug("Unable to start client: {}", throwable);
        Platform.runLater(() -> loginLayoutController.tellUserAboutBadConnection());
    }

    /**
     * Received user data for authentication, creates UserPassword DTO, end emit it into observable.
     * countDownLatch.await() blocks current thread, until authModelEmitter will be initialized.
     * @param userNameString string that contains user name
     * @param userPasswordString string that contains user password
     */
    public void sendUserAuthData(String userNameString, String userPasswordString) {
        AuthModel authModel = new AuthModel(userNameString, userPasswordString);
        try {
            if (authModelEmitter ==  null) {
                countDownLatch.await();
            }
            authModelEmitter.onNext(authModel);
        } catch (InterruptedException e) {
            logger.debug("Something went wrong with sending user auth data to server: {}", e);
            loginLayoutController.tellUserAboutBadConnection();
        }
    }

    /**
     * Method for UI to close current authentication session im emergency case.
     */
    public void closeSession(){
        if(socketDisposable != null && !socketDisposable.isDisposed()) {
            socketDisposable.dispose();
            logger.debug("socketDisposable is disposed: {}", socketDisposable.isDisposed());
        }
    }
}
