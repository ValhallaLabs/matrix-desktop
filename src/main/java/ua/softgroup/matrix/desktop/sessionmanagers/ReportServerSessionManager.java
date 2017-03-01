package ua.softgroup.matrix.desktop.sessionmanagers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.softgroup.matrix.desktop.utils.CommandExecutioner;
import ua.softgroup.matrix.desktop.utils.SocketProvider;
import ua.softgroup.matrix.server.desktop.api.ServerCommands;
import ua.softgroup.matrix.server.desktop.model.datamodels.ReportModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus;

import java.io.*;
import java.net.Socket;
import java.util.Set;


/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ReportServerSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(ReportServerSessionManager.class);
    private  ResponseModel<ReportModel> responseModel;
    private ReportModel reportModel;
    private Set<ReportModel> setReportModel;
    private Socket socket;
    private String responseServer;
    private CommandExecutioner commandExecutioner = new CommandExecutioner();

    public void saveOrChangeReportOnServer(ReportModel reportModel) throws IOException {
        socket = openSocketConnection();
        saveOrChangeReport(reportModel);
        serverReportResponse(responseServer);
        closeSocketConnection(socket);
    }

    public Set<ReportModel> sendProjectDataAndGetReportById(long id) throws IOException, ClassNotFoundException {
        socket = openSocketConnection();
        getProjectReportsById(id);
        setReportModelToCollection();
        closeSocketConnection(socket);
        return setReportModel;

    }

    private void serverReportResponse(String response) {
        logger.debug("Report Response");
        if (ResponseStatus.REPORT_EXISTS.name().equals(response)) {
            System.out.println("Report Already create");
        }
    }

    private void saveOrChangeReport(ReportModel reportmodel) throws IOException {
        commandExecutioner.sendCommand(socket, ServerCommands.SAVE_REPORT, reportmodel, reportmodel.getProjectId());
        responseServer = new DataInputStream(socket.getInputStream()).readUTF();
    }

    private void getProjectReportsById(long id) throws IOException {
        commandExecutioner.sendCommand(socket,ServerCommands.GET_REPORTS,id);
        logger.debug("Get Report by project Id");
    }

    private void setReportModelToCollection() throws IOException, ClassNotFoundException {
        try {
           responseModel= commandExecutioner.getResponse(socket);
            logger.debug("Set Report Model to Current Session successfully");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.debug("Unable get report From Input Stream");
        }
     if(responseModel.getResponseStatus()==ResponseStatus.SUCCESS){
         reportModel =responseModel.getContainer().get();
         setReportModel.add(reportModel);
     }

    }

    private Socket openSocketConnection() throws IOException {
        Socket socket = SocketProvider.openNewConnection();
        logger.debug("Open socket connection");
        return socket;
    }

    private void closeSocketConnection(Socket socket) throws IOException {
        commandExecutioner.sendCommand(socket, ServerCommands.CLOSE);
        socket.close();
        logger.debug("Close socket connection");
    }

}
