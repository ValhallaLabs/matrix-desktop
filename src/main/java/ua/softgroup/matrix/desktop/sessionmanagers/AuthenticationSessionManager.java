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
import ua.softgroup.matrix.desktop.start.Main;
import ua.softgroup.matrix.desktop.utils.SocketProvider;
import ua.softgroup.matrix.server.desktop.api.Constants;
import ua.softgroup.matrix.server.desktop.api.ServerCommands;
import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.TokenModel;
import ua.softgroup.matrix.server.desktop.model.UserPassword;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private ObjectInputStream objectInputStream;
    private DataInputStream dataInputStream;

    Disposable subscription;

    private Emitter createNewObservable() {
        Observable<Set<ProjectModel>> observable = Observable.using(this::createSocketConnection, this::createObservable, this::closeSocketConnection);
        subscription = observable.subscribe(projectModels -> {
            System.out.println(projectModels);
        }, throwable -> {
            throwable.printStackTrace();
        }, ()->{
        });
        return userPasswordEmitter;
    }

    //Resource factory
    private Socket createSocketConnection() throws IOException {
        logger.debug("Current time: {}", "Open Connection");
        return SocketProvider.openNewConnection();
    }

    //Dispose Action
    private void closeSocketConnection(Socket socket) throws IOException {
        objectOutputStream.writeObject(ServerCommands.CLOSE);
        socket.close();
//        objectOutputStream.close();
//        objectInputStream.close();
//        dataInputStream.close();
        logger.debug("Current time: {}", "Connection closed");
    }

    //ObservableFactory
    private Observable createObservable(Socket socket) {
        return Observable.create((ObservableOnSubscribe<UserPassword>) e -> userPasswordEmitter = e)
                .map(userPassword -> authenticateUser(userPassword, socket))
                .map(this::handleServerAuthResponse)
                .retry()
                .map(tokenModel -> getAllProjects(tokenModel, socket))
                ;
    }

    private String authenticateUser(UserPassword userPassword, Socket socket) throws IOException {
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(ServerCommands.AUTHENTICATE);
        objectOutputStream.writeObject(userPassword);
        dataInputStream = new DataInputStream(socket.getInputStream());
        return dataInputStream.readUTF();
    }

    private TokenModel handleServerAuthResponse(String response) {
        System.out.println(Constants.INVALID_USERNAME.name());
        if (!Constants.INVALID_USERNAME.name().equals(response) &&
                !Constants.INVALID_PASSWORD.equals(response)) {
            TokenModel tokenModel = new TokenModel();
            tokenModel.setToken(response);
            System.out.println(tokenModel.getToken());
            return tokenModel;
        } else {
            //TODO: Tell user, that login is incorrect
            throw new RuntimeException();
        }

    }

    private Object getAllProjects(TokenModel tokenModel, Socket socket) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject(ServerCommands.GET_ALL_PROJECT);
        objectOutputStream.writeObject(tokenModel);
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        return objectInputStream.readObject();
    }
    //TEMPORARY
    public static void main(String[] args) {
        AuthenticationSessionManager authenticationSessionManager = new AuthenticationSessionManager();
        Emitter emitter = authenticationSessionManager.createNewObservable();
        UserPassword userPassword = new UserPassword();
        userPassword.setUsername("supasdas");
        userPassword.setPassword("asdf");
        emitter.onNext(userPassword);

        UserPassword userPassword1 = new UserPassword();
        userPassword1.setUsername("sup");
        userPassword1.setPassword("asdf");
        emitter.onNext(userPassword1);





        authenticationSessionManager.subscription.dispose();
    }
}
