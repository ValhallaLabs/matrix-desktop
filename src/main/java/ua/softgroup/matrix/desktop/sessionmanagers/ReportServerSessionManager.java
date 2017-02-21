package ua.softgroup.matrix.desktop.sessionmanagers;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.utils.SocketProvider;
import ua.softgroup.matrix.server.desktop.api.ServerCommands;
import ua.softgroup.matrix.server.desktop.model.ReportModel;
import ua.softgroup.matrix.server.desktop.model.TokenModel;
import java.io.*;
import java.net.Socket;
import java.util.Set;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ReportServerSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(ReportServerSessionManager.class);
    private ObjectOutputStream objectOutputStream;
    private DataOutputStream dataOutputStems;
    private Set<ReportModel> projectReport;
    private Socket socket;

    public Set<ReportModel> sendProjectData(long id) throws IOException {
        socket = openSocketConnection();
        initOutputStreams();
        getProjectReportById(id);
        setReportModelToCurrentSessionInfo();
        closeSocketConnection(socket);
        return projectReport;
    }

    private void initOutputStreams() throws IOException {
        OutputStream output = socket.getOutputStream();
        objectOutputStream = new ObjectOutputStream(output);
        dataOutputStems = new DataOutputStream(output);
    }

    private void getProjectReportById(long id) throws IOException {
        objectOutputStream.writeObject(ServerCommands.GET_REPORTS_BY_PROJECT_ID);
        objectOutputStream.flush();
        TokenModel tokenModel = CurrentSessionInfo.getTokenModel();
        objectOutputStream.writeObject(tokenModel);
        objectOutputStream.flush();
        dataOutputStems.writeLong(id);
        dataOutputStems.flush();
        logger.debug("Get Report by project Id");
    }

    private void setReportModelToCurrentSessionInfo() throws IOException {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        try {
            projectReport = (Set<ReportModel>) objectInputStream.readObject();
            System.out.println(projectReport);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.debug("Unable get report From Input Stream");
        }
        logger.debug("Set Report Model to Current Session");
    }

    private Socket openSocketConnection() throws IOException {
        Socket socket = SocketProvider.openNewConnection();
        logger.debug("Open socket connection");
        return socket;
    }

    protected void closeSocketConnection(Socket socket) throws IOException {
        objectOutputStream.writeObject(ServerCommands.CLOSE);
        objectOutputStream.flush();
        socket.close();
        logger.debug("Close socket connection");
    }

}
