package ua.softgroup.matrix.desktop.utils;

import ua.softgroup.matrix.api.ServerCommands;
import ua.softgroup.matrix.api.model.datamodels.DataModel;
import ua.softgroup.matrix.api.model.requestmodels.RequestModel;
import ua.softgroup.matrix.api.model.responsemodels.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static ua.softgroup.matrix.api.model.responsemodels.ResponseStatus.SUCCESS;


/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class CommandExecutioner {
    public static final Logger logger = LoggerFactory.getLogger(CommandExecutioner.class);
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    /**
     * Method for sending commands to server for a specific project without an attachment with returning response.
     * Creates {@link RequestModel} for specified type and calls main sendCommandWithResponse method.
     * @param serverCommand specific request command for the server
     * @param projectId a long primitive
     * @param <T1> type of {@link DataModel} in {@link RequestModel}
     * @param <T2> type of {@link DataModel} in {@link ResponseModel}
     * @return dataModel received from main sendCommandWithResponse method.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public <T1 extends DataModel, T2 extends DataModel> T2 sendCommandWithResponse
            (ServerCommands serverCommand, long projectId) throws IOException, ClassNotFoundException, FailResponseException {
        return this.<T1,T2>sendCommandWithResponse(serverCommand, new RequestModel<T1>(
                CurrentSessionInfo.getToken(), projectId));
    }

    /**
     * Method for sending commands to server with an attached {@link DataModel} for a specific project with returning
     * response. Creates {@link RequestModel} for specified type and calls main sendCommandWithResponse method.
     * @param serverCommand specific request command for the server
     * @param projectId a long primitive
     * @param dataModel a DTO for a specific command
     * @param <T1> type of {@link DataModel} in {@link RequestModel}
     * @param <T2> type of {@link DataModel} in {@link ResponseModel}
     * @return dataModel received from main sendCommandWithResponse method.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public <T1 extends DataModel, T2 extends DataModel> T2 sendCommandWithResponse
            (ServerCommands serverCommand, long projectId, T1 dataModel) throws IOException, ClassNotFoundException, FailResponseException {
        return this.<T1,T2>sendCommandWithResponse(serverCommand, new RequestModel<T1>(
                CurrentSessionInfo.getToken(), projectId, dataModel));
    }

    /**
     * Method for sending commands to server with returning response. It opens a new socket connection with server,
     * sends the command to the server using the method sendCommand. Then it return a response received through the
     * getResponse method. In case of a ResponseStatus "FAIL" it may throw {@link NullPointerException}.
     * @param serverCommand specific request command for the server
     * @param requestModel model that may contain token, project id, and {@link DataModel}
     * @param <T1> type of {@link DataModel} in {@link RequestModel}
     * @param <T2> type of {@link DataModel} in {@link ResponseModel}
     * @return dataModel received from getResponse method
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private <T1 extends DataModel, T2 extends DataModel> T2 sendCommandWithResponse(
            ServerCommands serverCommand, RequestModel requestModel) throws IOException, ClassNotFoundException, FailResponseException {
        openSocketConnection();
        sendCommand(serverCommand, requestModel);
        return this.<T2>getResponse(socket);
    }

    /**
     * Method for sending commands to server without an attachment and returning response.
     * Creates {@link RequestModel} for specified type and calls main sendCommandWithNoResponse method.
     * @param serverCommand specific request command for the server
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void sendCommandWithNoResponse(ServerCommands serverCommand) throws IOException, ClassNotFoundException, FailResponseException {
        sendCommandWithNoResponse(serverCommand, new RequestModel(CurrentSessionInfo.getToken(), -1));
    }

    /**
     * Method for sending commands to server for a specific project without an attachment and returning response.
     * Creates {@link RequestModel} for specified type and calls main sendCommandWithNoResponse method.
     * @param serverCommand specific request command for the server
     * @param projectId a long primitive
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void sendCommandWithNoResponse(ServerCommands serverCommand, long projectId)
            throws IOException, ClassNotFoundException, FailResponseException {
        sendCommandWithNoResponse(serverCommand, new RequestModel(CurrentSessionInfo.getToken(), projectId));
    }

    /**
     * Method for sending commands to server with an attached {@link DataModel} for a specific project without returning
     * response. Creates {@link RequestModel} for specified type and calls main sendCommandWithNoResponse method.
     * @param serverCommand specific request command for the server
     * @param dataModel a DTO for a specific command
     * @param projectId a long primitive
     * @param <T> type of {@link DataModel}
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public <T extends DataModel> void sendCommandWithNoResponse
            (ServerCommands serverCommand, T dataModel, long projectId) throws IOException, ClassNotFoundException, FailResponseException {
        sendCommandWithNoResponse(
                serverCommand, new RequestModel<T>(CurrentSessionInfo.getToken(), projectId, dataModel));
    }

    /**
     * Method for sending commands to server without returning response. It opens a new socket connection with server,
     * sends the command to the server using the method sendCommand. Then it receive a response from the server and
     * close socket. In case of the ResponseStatus "FAIL" it may throw {@link NullPointerException}.
     * @param serverCommand specific request command for the server
     * @param requestModel model that may contain token, project id, and {@link DataModel}
     * @param <T> type of {@link DataModel}
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private <T extends DataModel> void sendCommandWithNoResponse(
            ServerCommands serverCommand, RequestModel<T> requestModel) throws IOException, ClassNotFoundException, FailResponseException {
        openSocketConnection();
        sendCommand(serverCommand, requestModel);
        getResponse(socket);
    }

    /**
     * Method for sending commands to server. It receives socket and opens {@link ObjectOutputStream} for sending
     * a command and requestModel.
     * @param serverCommand specific request command for the server
     * @param requestModel model that may contain token, project id, and {@link DataModel}
     * @param <T> type of {@link DataModel}
     * @throws IOException
     */
    private <T extends DataModel> void sendCommand(ServerCommands serverCommand, RequestModel<T> requestModel)
            throws IOException {
        logger.debug("Server command: {}. Request model: {}", serverCommand, requestModel.toString());
        objectOutputStream.writeObject(serverCommand);
        objectOutputStream.writeObject(requestModel);
        objectOutputStream.flush();
    }

    /**
     * Method for retrieving {@link ResponseModel} from server as an answer to client request.
     * @param socket current socket connection.
     * @param <T> type of {@link DataModel} that may be contained in the {@link ResponseModel}
     * @return responseModel with
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private <T extends DataModel> T getResponse(Socket socket) throws IOException, ClassNotFoundException, FailResponseException {
        ResponseModel<T> responseModel = (ResponseModel<T>) objectInputStream.readObject();
        logger.debug("Response: {}", responseModel.toString());
        closeSocketConnection();
        if (responseModel.getResponseStatus() == SUCCESS && responseModel.getContainer().isPresent()) {
            return responseModel.getContainer().get();
        }
        throw new FailResponseException();
    }

    private void openSocketConnection() throws IOException {
        socket = SocketProvider.openNewConnection();
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        logger.info("Connection is opened");
    }

    private void closeSocketConnection() throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject(ServerCommands.CLOSE);
        objectOutputStream.flush();
        socket.close();
        logger.info("Connection is closed");
    }

    public class FailResponseException extends Exception {
        public FailResponseException() { super(); }
        public FailResponseException(String message) { super(message); }
        public FailResponseException(String message, Throwable cause) { super(message, cause); }
        public FailResponseException(Throwable cause) { super(cause); }
    }
}
