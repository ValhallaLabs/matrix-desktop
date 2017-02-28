package ua.softgroup.matrix.desktop.sessionmanagers;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.LoginLayoutController;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.utils.SocketProvider;
import ua.softgroup.matrix.server.desktop.api.ServerCommands;
import ua.softgroup.matrix.server.desktop.model.datamodels.AuthModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.InitializeModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;


/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class AuthenticationServerSessionManager {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServerSessionManager.class);
    private LoginLayoutController loginLayoutController;
    private Emitter<AuthModel> authModelEmitter;
    private Disposable disposableSubscription;
    private CountDownLatch countDownLatch;
    private ObjectOutputStream objectOutputStream;

    public AuthenticationServerSessionManager(LoginLayoutController loginLayoutController) {
        this.loginLayoutController = loginLayoutController;
        countDownLatch = new CountDownLatch(1);
        disposableSubscription = Observable.using(this::openSocketConnection, this::createBindedObservable, this::closeSocketConnection)
                .subscribe(this::finishAuthentication, this::handleExceptions);
    }

    /**
     * The factory function to create a resource object that depends on the Observable.
     * Creates a new socket connection with server.
     * @return a new socket connection
     */
    private Socket openSocketConnection() throws IOException {
        Socket socket = SocketProvider.openNewConnection();
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        logger.debug("Open socket connection");
        return socket;
    }

    /**
     * The factory function to create an Observable, that executes process of authentication
     * and gets all projects of user.
     * @param socket The socket which is dependent to an observable
     * @return Observable
     */
    private Observable<InitializeModel> createBindedObservable(Socket socket) {
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
        logger.debug("Socket connection closed");
        objectOutputStream.writeObject(ServerCommands.CLOSE);
        objectOutputStream.flush();
        socket.close();
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
    private InitializeModel authenticateUser(AuthModel authModel, Socket socket) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject(ServerCommands.AUTHENTICATE);
        objectOutputStream.writeObject(authModel);
        objectOutputStream.flush();
        return (InitializeModel) new ObjectInputStream(socket.getInputStream()).readObject();
    }

    /**
     * Checks {@link InitializeModel} response status.
     * @param initializeModel DTO which may contains all start settings and info in case of success
     * @return boolean result of authentication
     */
    private boolean handleServerAuthResponse(InitializeModel initializeModel) {
        logger.debug("Auth response status {}", initializeModel.responseStatus);
        if (ResponseStatus.SUCCESS != initializeModel.responseStatus) {
            loginLayoutController.errorLoginPassword();
            return false;
        }
        return true;
    }

    /**
     * Saves {@link InitializeModel} to {@link CurrentSessionInfo}.
     * Tells {@link LoginLayoutController} to open main window and dispose current session.
     * @param initializeModel DTO which contains all start settings and info
     */
    private void finishAuthentication(InitializeModel initializeModel){
        CurrentSessionInfo.setInitializeModel(initializeModel);
        disposableSubscription.dispose();
        loginLayoutController.closeLoginLayoutAndStartMainLayout();
        logger.debug("Authentication completed");
    }

    /**
     * Handles exception which may be thrown while session is executing.
     * @param throwable possible exception
     */
    private void handleExceptions(Throwable throwable) {
        logger.debug("Unable to start client: {}", throwable);
        loginLayoutController.tellUserAboutBadConnection();
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
        if(!disposableSubscription.isDisposed()) {
            disposableSubscription.dispose();
        }
    }
}
