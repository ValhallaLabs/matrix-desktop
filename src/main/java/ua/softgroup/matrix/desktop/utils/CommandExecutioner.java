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

import static ua.softgroup.matrix.server.desktop.api.ServerCommands.*;
import static ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus.SUCCESS;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class CommandExecutioner {

    public void sendRawCommand(Socket socket, ServerCommands serverCommand) throws IOException {
        sendCommand(socket, serverCommand, new RequestModel(CurrentSessionInfo.getToken(), -1));
    }

    public <T extends DataModel> void sendRawCommand(Socket socket, ServerCommands serverCommand, T dataModel)
            throws IOException {
        sendCommand(socket, serverCommand, new RequestModel<T>(CurrentSessionInfo.getToken(), dataModel));
    }

    public  <T extends DataModel> ResponseModel<T> getRawResponseModel(Socket socket)
            throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        return (ResponseModel<T>) objectInputStream.readObject();
    }

    public <T1 extends DataModel, T2 extends DataModel> T2 sendCommandWithResponse
            (ServerCommands serverCommands, long projectId) throws IOException, ClassNotFoundException {
        return this.<T1,T2>sendCommandWithResponse(serverCommands, new RequestModel<T1>(
                CurrentSessionInfo.getToken(), projectId));
    }

    public <T1 extends DataModel, T2 extends DataModel> T2 sendCommandWithResponse
            (ServerCommands serverCommands, long projectId, T1 dataModel) throws IOException, ClassNotFoundException {
        return this.<T1,T2>sendCommandWithResponse(serverCommands, new RequestModel<T1>(
                CurrentSessionInfo.getToken(), projectId, dataModel));
    }

    private <T1 extends DataModel, T2 extends DataModel> T2 sendCommandWithResponse(
            ServerCommands serverCommands, RequestModel requestModel) throws IOException, ClassNotFoundException {
        Socket socket = SocketProvider.openNewConnection();
        sendCommand(socket, serverCommands, requestModel);
        return this.getResponse(socket);
    }

    public void sendCommandWithNoResponse(ServerCommands serverCommand) throws IOException, ClassNotFoundException {
        sendCommandWithNoResponse(serverCommand, new RequestModel(CurrentSessionInfo.getToken(), -1));
    }

    public void sendCommandWithNoResponse(ServerCommands serverCommand, long projectId)
            throws IOException, ClassNotFoundException {
        sendCommandWithNoResponse(serverCommand, new RequestModel(CurrentSessionInfo.getToken(), projectId));
    }

    public <T extends DataModel> void sendCommandWithNoResponse
            (ServerCommands serverCommand, T dataModel, long projectId) throws IOException, ClassNotFoundException {
        this.sendCommandWithNoResponse(
                serverCommand, new RequestModel<T>(CurrentSessionInfo.getToken(), projectId, dataModel));
    }

    private <T extends DataModel> void sendCommandWithNoResponse(
            ServerCommands serverCommand, RequestModel<T> requestModel) throws IOException, ClassNotFoundException {
        Socket socket = SocketProvider.openNewConnection();
        sendCommand(socket, serverCommand, requestModel);
        ResponseModel responseModel = getResponse(socket);
        sendRawCommand(socket, CLOSE);
        socket.close();
        if (SUCCESS != responseModel.getResponseStatus()){
            throw new NullPointerException();
        }
    }

    private <T extends DataModel> void sendCommand(
            Socket socket, ServerCommands serverCommand, RequestModel<T> requestModel) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(serverCommand);
        objectOutputStream.writeObject(requestModel);
        objectOutputStream.flush();
    }

    private <T extends DataModel> T getResponse(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        ResponseModel<T> responseModel = (ResponseModel<T>) objectInputStream.readObject();
        sendRawCommand(socket, CLOSE);
        socket.close();
        if (responseModel.getResponseStatus() == SUCCESS && responseModel.getContainer().isPresent()) {
            return responseModel.getContainer().get();
        }
        return null;
    }
}
