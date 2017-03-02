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
    private Set<ReportModel> setReportModel;
    private Socket socket;
    private CommandExecutioner commandExecutioner;


    private Socket openSocketConnection() throws IOException {
        Socket socket = SocketProvider.openNewConnection();
        commandExecutioner=new CommandExecutioner();
        logger.debug("Open socket connection");
        return socket;
    }

    public void saveOrChangeReportOnServer(ReportModel reportModel) throws IOException {
        socket = openSocketConnection();
        saveOrChangeReport(reportModel);
        closeSocketConnection(socket);
        logger.debug("Save or change report on server");
    }

    public Set<ReportModel> sendProjectDataAndGetReportById(long id) throws IOException, ClassNotFoundException {
        socket = openSocketConnection();
        getProjectReportsById(id);
        setResponceModelToCollection();
        closeSocketConnection(socket);
        logger.debug("Get report by id from server");
        return setReportModel;

    }

    private void saveOrChangeReport(ReportModel reportmodel) throws IOException {
//        commandExecutioner.sendCommand(socket, ServerCommands.SAVE_REPORT, reportmodel, reportmodel.getProjectId());
        logger.debug("Send save or change report to server");
    }

    private void getProjectReportsById(long id) throws IOException {
//        commandExecutioner.sendCommand(socket,ServerCommands.GET_REPORTS,id);
        logger.debug("Send id project to server");
    }

    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
    private void setResponceModelToCollection() throws IOException, ClassNotFoundException {
        try {
           responseModel= commandExecutioner.getResponse(socket);
            logger.debug("Set Report Model to Current Session successfully");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.debug("Unable get report From Input Stream");
        }
     if(responseModel.getResponseStatus()==ResponseStatus.SUCCESS){
         if(responseModel.getContainer().isPresent()){
             setReportModel=(Set<ReportModel>)(responseModel.getContainer().get());
         }
     }

    }

    private void closeSocketConnection(Socket socket) throws IOException {
        logger.debug("Close socket connection");
//        commandExecutioner.sendCommand(socket, ServerCommands.CLOSE);
        socket.close();

    }

}
