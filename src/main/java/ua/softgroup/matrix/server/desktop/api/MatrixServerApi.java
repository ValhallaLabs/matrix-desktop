package ua.softgroup.matrix.server.desktop.api;

import ua.softgroup.matrix.server.desktop.model.ActiveWindowsModel;
import ua.softgroup.matrix.server.desktop.model.ClientSettingsModel;
import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.ReportModel;
import ua.softgroup.matrix.server.desktop.model.ScreenshotModel;
import ua.softgroup.matrix.server.desktop.model.SynchronizedModel;
import ua.softgroup.matrix.server.desktop.model.TimeModel;
import ua.softgroup.matrix.server.desktop.model.TokenModel;
import ua.softgroup.matrix.server.desktop.model.UserPassword;
import ua.softgroup.matrix.server.desktop.model.WriteKeyboard;

import java.util.Set;

public interface MatrixServerApi {

    /**
     * Tries to authenticate a user using the given credentials
     *
     * @param userPassword DTO with username and password
     * @return a token in the case of successful authentication, 'invalid credentials' otherwise
     */
    String authenticate(UserPassword userPassword);

    Set<ProjectModel> getUserActiveProjects(TokenModel tokenModel);

    /**
     * Saves a report for the given date
     *
     * @param reportModel report's model
     * @return an id of the saved report
     */
    Constants saveReport(ReportModel reportModel);

    Set<ReportModel> getAllReports(TokenModel tokenModel);

    Set<ReportModel> getAllReportsByProjectId(TokenModel tokenModel, long projectId);

    void startWork(TimeModel timeModel);

    void endWork(TimeModel timeModel);

    void startDowntime(TimeModel downTimeModel);

    void endDowntime(TimeModel downTimeModel);

    TimeModel getTodayWorkTime(TimeModel timeModel);

    TimeModel getTotalWorkTime(TimeModel timeModel);

    boolean sync(SynchronizedModel synchronizedModel);

    boolean isClientSettingsUpdated(long settingsVersion);

    ClientSettingsModel getClientSettings();

    void saveKeyboardLog(WriteKeyboard writeKeyboard);

    void saveActiveWindowsLog(ActiveWindowsModel activeWindows);

    void saveScreenshot(ScreenshotModel file);

}
