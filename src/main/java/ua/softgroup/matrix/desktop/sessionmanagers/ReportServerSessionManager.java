package ua.softgroup.matrix.desktop.sessionmanagers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.utils.SocketProvider;
import ua.softgroup.matrix.server.desktop.api.Constants;
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
    private static Set<ReportModel> projectReport;
    private Socket socket;
    private String responseServer;

    public void saveReportToServer(ReportModel reportModel) throws IOException {
        socket = openSocketConnection();
        initOutputStreams();
        saveReport(reportModel);
        serverReportResponse(responseServer);
        closeSocketConnection(socket);
    }

    public void changeReportOnServer(ReportModel reportModel) throws IOException {
        socket = openSocketConnection();
        initOutputStreams();
        saveReport(reportModel);
        closeSocketConnection(socket);
    }

    public Set<ReportModel> sendProjectDataAndGetReportById(long id) throws IOException {
        socket = openSocketConnection();
        initOutputStreams();
        getProjectReportsById(id);
        setReportModelToCollection();
        closeSocketConnection(socket);
        return projectReport;

    }

    private void serverReportResponse(String response) {
        logger.debug("Report Response");
        if (Constants.REPORT_EXISTS.name().equals(response)) {
            System.out.println("Report Already create");
        }
    }

    private void saveReport(ReportModel reportmodel) throws IOException {
        objectOutputStream.writeObject(ServerCommands.SAVE_REPORT);
        objectOutputStream.writeObject(reportmodel);
        objectOutputStream.flush();
        responseServer = new DataInputStream(socket.getInputStream()).readUTF();
    }

    private void initOutputStreams() throws IOException {
        OutputStream output = socket.getOutputStream();
        objectOutputStream = new ObjectOutputStream(output);
        dataOutputStems = new DataOutputStream(output);
    }

    private void getProjectReportsById(long id) throws IOException {
        objectOutputStream.writeObject(ServerCommands.GET_REPORTS_BY_PROJECT_ID);
        objectOutputStream.flush();
        TokenModel tokenModel = CurrentSessionInfo.getTokenModel();
        objectOutputStream.writeObject(tokenModel);
        objectOutputStream.flush();
        dataOutputStems.writeLong(id);
        dataOutputStems.flush();
        logger.debug("Get Report by project Id");
    }

    private void setReportModelToCollection() throws IOException {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        try {
            projectReport = (Set<ReportModel>) objectInputStream.readObject();
            logger.debug("Set Report Model to Current Session successfully" + projectReport);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.debug("Unable get report From Input Stream");
        }

    }

    private Socket openSocketConnection() throws IOException {
        Socket socket = SocketProvider.openNewConnection();
        logger.debug("Open socket connection");
        return socket;
    }

    private void closeSocketConnection(Socket socket) throws IOException {
        objectOutputStream.writeObject(ServerCommands.CLOSE);
        objectOutputStream.flush();
        socket.close();
        logger.debug("Close socket connection");
    }

}
