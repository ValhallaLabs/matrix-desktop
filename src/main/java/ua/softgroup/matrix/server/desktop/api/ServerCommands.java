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

public enum ServerCommands {

    /**
     * The command for a user authentication. The server expects to read the {@link UserPassword} object
     * and then return a string token in the case of successful authentication,
     * {@link Constants#INVALID_USERNAME}/{@link Constants#INVALID_PASSWORD} otherwise.
     */
    AUTHENTICATE,

    /**
     * The command for retrieving a user's active projects. The server expects to read the {@link TokenModel} object
     * and then return a set of {@link ProjectModel}'s.
     */
    GET_ALL_PROJECT,

    /**
     * The command for saving/editing a user's report. The server expects to read the {@link ReportModel} object
     * and then return {@link Constants#REPORT_EXISTS} if user already saved a report today
     * or {@link Constants#REPORT_EXPIRED} if user tried to edit a report that expired,
     * {@link Constants#TOKEN_VALIDATED} otherwise.
     */
    SAVE_REPORT,

    /**
     * The command for retrieving a user's all reports. The server expects to read the {@link TokenModel} object
     * and then return a set of {@link ReportModel}'s.
     */
    GET_ALL_REPORTS,

    /**
     * The command for retrieving a user's reports of the specified project. The server expects
     * to read the {@link TokenModel} object and the project' id as a primitive {@code long} value sequentially.
     * Then the server returns a set of {@link ReportModel}'s.
     */
    GET_REPORTS_BY_PROJECT_ID,

    /**
     * The command for saving a user's screenshot. The server expects to read the {@link ScreenshotModel} object
     * and then return nothing.
     */
    SAVE_SCREENSHOT,

    /**
     * That command indicates that a user starts his work. The server expects to read the {@link TimeModel} object
     * and then return nothing.
     */
    START_WORK,

    /**
     * That command indicates that a user ends his work. The server expects to read the {@link TimeModel} object
     * and then return nothing.
     */
    END_WORK,

    /**
     * The command for retrieving total time of the specified user's project. The server expects to read
     * the {@link TimeModel} object and then return the {@link TimeModel} object with hours/minutes and
     * a percentage of idling.
     */
    GET_TOTAL_TIME,

    /**
     * The command for retrieving today's time of the specified user's project. The server expects to read
     * the {@link TimeModel} object and then return the {@link TimeModel} object with hours/minutes.
     */
    GET_TODAY_TIME,

    /**
     * The command for checking if available new setting. ABSOLUTELY MEANINGLESS AND NEEDLESS.
     * The server expects to read a primitive {@code long} value and return a primitive {@code boolean} value.
     */
    CHECK_UPDATE_SETTING,

    /**
     * The command for updating client's settings. The server returns the {@link ClientSettingsModel} object.
     */
    UPDATE_SETTING,

    /**
     * The command for syncing with client after offline. The server expects to read
     * the {@link SynchronizedModel} object and then return a primitive {@code boolean} flag of success/failure.
     */
    SYNCHRONIZED,

    /**
     * That command indicates that starts idling. The server expects to read the {@link TimeModel} object
     * and then return nothing.
     */
    START_DOWNTIME,

    /**
     * That command indicates that ends idling. The server expects to read the {@link TimeModel} object
     * and then return nothing.
     */
    STOP_DOWNTIME,

    /**
     * The command for saving a user's keyboard log. The server expects to read the {@link WriteKeyboard} object
     * and then return nothing.
     */
    KEYBOARD_LOG,

    /**
     * The command for saving a user's active windows. The server expects to read
     * the {@link ActiveWindowsModel} object and then return nothing.
     */
    ACTIVE_WINDOWS_LOG,

    /**
     * That command close socket connection.
     */
    CLOSE

}
