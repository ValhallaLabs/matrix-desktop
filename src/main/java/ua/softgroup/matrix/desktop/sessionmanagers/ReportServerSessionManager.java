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
    private Set<ReportModel> setReportModel;
    private CommandExecutioner commandExecutioner;

    public ReportServerSessionManager() {
        commandExecutioner=new CommandExecutioner();
    }

    /**
     * Send to {@link CommandExecutioner} command for save or change report on Server
     * @param reportModel  reportModel current report get from user choice
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void saveOrChangeReportOnServer(ReportModel reportModel) throws IOException, ClassNotFoundException {
        commandExecutioner.sendCommandWithNoResponse(ServerCommands.SAVE_REPORT, reportModel, reportModel.getId());
        logger.debug("Save or change report on server");
    }

    /**
     * Send to {@link CommandExecutioner} command for get all report for this project
     * @param id  id current project get from user choice
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Set<ReportModel> sendProjectDataAndGetReportById(long id) throws IOException, ClassNotFoundException {
        setReportModel = commandExecutioner.sendCommandWithResponse(ServerCommands.GET_REPORTS, id);
        logger.debug("Get report by id from server");
        return setReportModel;
    }

}
