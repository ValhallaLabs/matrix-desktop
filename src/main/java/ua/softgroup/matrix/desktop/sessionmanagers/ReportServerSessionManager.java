package ua.softgroup.matrix.desktop.sessionmanagers;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.controllerjavafx.ReportLayoutController;
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
    private static ReportLayoutController reportLayoutController;
    private static final Logger logger = LoggerFactory.getLogger(ReportServerSessionManager.class);
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private DataOutputStream dataOutputStems;
    private Set<ReportModel> reportModelsa;
    private Socket socket;

    public ReportServerSessionManager(ReportLayoutController reportLayoutController) throws IOException {
        this.reportLayoutController = reportLayoutController;
    }

    public Set<ReportModel> sendProjectData(long id) throws IOException {
        socket = openSocketConnection();
        initOutputStreams();
        getProjectReportById(id);
        setReportModelToCurrentSessionInfo();
        closeSocketConnection(socket);
        return reportModelsa;
    }

    private void initOutputStreams() throws IOException {
        OutputStream output = socket.getOutputStream();
        objectOutputStream = new ObjectOutputStream(output);
        dataOutputStems = new DataOutputStream(output);
    }

    private InputStream getProjectReportById(long id) throws IOException {
        objectOutputStream.writeObject(ServerCommands.GET_REPORTS_BY_PROJECT_ID);
        objectOutputStream.flush();
        TokenModel tokenModel = CurrentSessionInfo.getTokenModel();
        objectOutputStream.writeObject(tokenModel);
        objectOutputStream.flush();
        dataOutputStems.writeLong(id);
        dataOutputStems.flush();
        logger.debug("Get Report by project Id");
        return socket.getInputStream();
    }

    private void setReportModelToCurrentSessionInfo() throws IOException {
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        try {
            reportModelsa = (Set<ReportModel>) objectInputStream.readObject();
            System.out.println(reportModelsa);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
