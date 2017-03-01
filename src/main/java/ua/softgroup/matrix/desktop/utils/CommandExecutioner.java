package ua.softgroup.matrix.desktop.utils;

import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.server.desktop.api.ServerCommands;
import ua.softgroup.matrix.server.desktop.model.datamodels.DataModel;
import ua.softgroup.matrix.server.desktop.model.requestmodels.RequestModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class CommandExecutioner {
    private long projectId = -1;

    public CommandExecutioner() {
    }

    public CommandExecutioner(long projectId) {
        this.projectId = projectId;
    }

    public void sendCommand(Socket socket, ServerCommands serverCommand) throws IOException {
        sendCommand(socket, serverCommand, new RequestModel(CurrentSessionInfo.getToken(), projectId));
    }

    public <T extends DataModel> void sendCommand(Socket socket, ServerCommands serverCommand, T dataModel) throws IOException {
        sendCommand(socket, serverCommand, new RequestModel<T>(CurrentSessionInfo.getToken(), projectId, dataModel));
    }

    private void sendCommand(Socket socket, ServerCommands serverCommand, RequestModel requestModel) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(serverCommand);
        objectOutputStream.writeObject(requestModel);
        objectOutputStream.flush();
    }

    private <T extends DataModel> ResponseModel<T> getResponse(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        return (ResponseModel<T>) objectInputStream.readObject();
    }
}
