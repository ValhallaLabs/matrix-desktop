package ua.softgroup.matrix.desktop.utils;

import ua.softgroup.matrix.server.desktop.api.ServerCommands;
import ua.softgroup.matrix.server.desktop.model.requestmodels.RequestModel;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class CommandExecutioner {

    public static void sendCommand(Socket socket, ServerCommands serverCommand, RequestModel requestModel) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(serverCommand);
        objectOutputStream.writeObject(requestModel);
        objectOutputStream.flush();
    }
}
