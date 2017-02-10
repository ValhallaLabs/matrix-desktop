package ua.softgroup.matrix.desktop.sessionmanagers;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.LoginLayoutController;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.start.Main;
import ua.softgroup.matrix.desktop.utils.SocketProvider;
import ua.softgroup.matrix.server.desktop.api.Constants;
import ua.softgroup.matrix.server.desktop.api.ServerCommands;
import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.TokenModel;
import ua.softgroup.matrix.server.desktop.model.UserPassword;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by Vadim on 10.02.2017.
 */
public class AuthenticationSessionManager {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private LoginLayoutController loginLayoutController;
    private Emitter<UserPassword> userPasswordEmitter;
    private ObjectOutputStream objectOutputStream;
    private DataInputStream dataInputStream;
    Disposable subscription;

    private Emitter createNewObservable() {
        Observable<Set<ProjectModel>> observable = Observable.using(this::createSocketConnection, this::createObservable, this::closeSocketConnection);
        subscription = observable.subscribe(
                this::setProjectsModelsToCurrentSessionInfo,
                throwable -> throwable.printStackTrace());
        return userPasswordEmitter;
    }

    private void setProjectsModelsToCurrentSessionInfo(Set<ProjectModel> projectModels){
        CurrentSessionInfo.setUserActiveProjects(projectModels);
        subscription.dispose();
    }

    //Resource factory
    private Socket createSocketConnection() throws IOException {
        Socket socket = SocketProvider.openNewConnection();
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
        logger.debug("Open socket connection");
        return socket;
    }

    //Dispose Action
    private void closeSocketConnection(Socket socket) throws IOException {
        logger.debug("Socket connection closed");
        objectOutputStream.writeObject(ServerCommands.CLOSE);
        objectOutputStream.flush();
        socket.close();
    }

    //ObservableFactory
    private Observable createObservable(Socket socket) {
        return Observable.create((ObservableOnSubscribe<UserPassword>) e -> userPasswordEmitter = e)
                .map(this::authenticateUser)
                .filter(this::handleServerAuthResponse)
                .map(this::composeTokenModel)
                .map(tokenModel -> getAllProjects(tokenModel, socket));
    }

    private String authenticateUser(UserPassword userPassword) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject(ServerCommands.AUTHENTICATE);
        objectOutputStream.writeObject(userPassword);
        objectOutputStream.flush();
        return dataInputStream.readUTF();
    }

    private Boolean handleServerAuthResponse(String response) {
        logger.debug("Auth response {}", response);
        Boolean tokenValidationResult = !Constants.INVALID_USERNAME.name().equals(response) &&
                !Constants.INVALID_PASSWORD.name().equals(response);
        if(!tokenValidationResult) {
            //TODO: Notify user about wrong login data
        }
        return tokenValidationResult;
    }

    private TokenModel composeTokenModel(String token) {
        TokenModel tokenModel = new TokenModel();
        tokenModel.setToken(token);
        return tokenModel;
    }

    private Object getAllProjects(TokenModel tokenModel, Socket socket) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject(ServerCommands.GET_ALL_PROJECT);
        objectOutputStream.writeObject(tokenModel);
        objectOutputStream.flush();
        return new ObjectInputStream(socket.getInputStream()).readObject();
    }

    //TEMPORARY
    public static void main(String[] args) {
        AuthenticationSessionManager authenticationSessionManager = new AuthenticationSessionManager();
        Emitter emitter = authenticationSessionManager.createNewObservable();
        logger.debug("Current time: {}", "1");
        UserPassword userPassword = new UserPassword();
        userPassword.setUsername("supasdas");
        userPassword.setPassword("asdf");
        emitter.onNext(userPassword);

        logger.debug("Current time: {}", "2");
        UserPassword userPassword1 = new UserPassword();
        userPassword1.setUsername("sup");
        userPassword1.setPassword("asdf");
        emitter.onNext(userPassword1);
    }
}
