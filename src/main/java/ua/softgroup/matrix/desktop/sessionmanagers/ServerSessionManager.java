package ua.softgroup.matrix.desktop.sessionmanagers;

import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.utils.SocketProvider;
import ua.softgroup.matrix.server.desktop.api.ServerCommands;
import java.io.*;
import java.net.Socket;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public abstract class ServerSessionManager {
    //TODO logger's class
    protected static final Logger logger = LoggerFactory.getLogger(AuthenticationServerSessionManager.class);
    protected ObjectOutputStream objectOutputStream;

    /**
     * Closes current authentication session
     */
    public abstract void closeSession();

    /**
     * The factory function to create a resource object that depends on the Observable.
     * Creates a new socket connection with server.
     * @return a new socket connection
     */
    protected Socket openSocketConnection() throws IOException {
        Socket socket = SocketProvider.openNewConnection();
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        logger.debug("Open socket connection");
        return socket;
    }

    /**
     * The function that will dispose of the resource, that depends to the Observable.
     * Sends command to server about closing connection and closes socket connection.
     * @param socket The socket which is dependent to an observable
     */
    protected void closeSocketConnection(Socket socket) throws IOException {
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
    // TODO bad practice
    protected abstract Observable<?> createBindedObservable(Socket socket);
}
