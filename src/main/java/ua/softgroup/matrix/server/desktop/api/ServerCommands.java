package ua.softgroup.matrix.server.desktop.api;

import ua.softgroup.matrix.server.desktop.model.datamodels.CheckPointModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.InitializeModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.SynchronizedModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.AuthModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.ReportModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.TimeModel;
import ua.softgroup.matrix.server.desktop.model.requestmodels.RequestModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseModel;
import ua.softgroup.matrix.server.desktop.model.responsemodels.ResponseStatus;

import java.util.Set;

public enum ServerCommands {

    /**
     * The command for a user authentication. The server expects to read the {@link RequestModel<AuthModel>} object
     * and then return a {@link ResponseModel<InitializeModel>} with a {@link ResponseStatus#SUCCESS}
     * in the case of successful authentication, a {@link ResponseStatus#INVALID_CREDENTIALS} otherwise.
     */
    AUTHENTICATE,

    /**
     * That command indicates that a user starts his work. The server expects to read the {@link RequestModel} object
     * and then return a {@link ResponseModel} with a {@link ResponseStatus#SUCCESS} in the case of successful start,
     * {@link ResponseStatus#FAIL} otherwise.
     */
    START_WORK,

    /**
     * That command indicates a period of user's work. The server expects to read the {@link RequestModel<CheckPointModel>}
     * object and then return a {@link ResponseModel<TimeModel>} with a {@link ResponseStatus#SUCCESS} in the case of
     * successful start, {@link ResponseStatus#FAIL} otherwise.
     */
    CHECK_POINT,

    /**
     * That command indicates that a user ends his work. The server expects to read the {@link RequestModel} object
     * and then return a {@link ResponseModel} with a {@link ResponseStatus#SUCCESS} in the case of successful start,
     * {@link ResponseStatus#FAIL} otherwise.
     */
    END_WORK,

    /**
     * That command indicates that starts idling. The server expects to read the {@link RequestModel} object
     * and then return a {@link ResponseModel} with a {@link ResponseStatus#SUCCESS} in the case of successful start,
     * {@link ResponseStatus#FAIL} otherwise.
     */
    START_IDLE,

    /**
     * That command indicates that ends idling. The server expects to read the {@link RequestModel} object
     * and then return a {@link ResponseModel} with a {@link ResponseStatus#SUCCESS} in the case of successful start,
     * {@link ResponseStatus#FAIL} otherwise.
     */
    STOP_IDLE,

    /**
     * The command for syncing with client after offline. The server expects to read the
     * {@link RequestModel<SynchronizedModel>} object and then return a {@link ResponseModel} with a
     * {@link ResponseStatus#SUCCESS} in the case of successful start, {@link ResponseStatus#FAIL} otherwise.
     */
    SYNCHRONIZED,

    /**
     * The command for retrieving a user's reports of the specified project. The server expects
     * to read the {@link RequestModel}. Then the server returns a {@link ResponseModel<Set<ReportModel>>} with a
     * {@link ResponseStatus#SUCCESS} in the case of successful start, {@link ResponseStatus#FAIL} otherwise.
     */
    GET_REPORTS,

    /**
     * The command for retrieving a user's active projects. The server expects to read the {@link RequestModel}.
     * Then the server returns a {@link ResponseModel<Set<ProjectModel>>} with a {@link ResponseStatus#SUCCESS}
     * in the case of successful start, {@link ResponseStatus#FAIL} otherwise.
     */
    GET_ALL_PROJECT,

    /**
     * The command for saving/editing a user's report. The server expects to read the {@link RequestModel<ReportModel>}
     * object and then return a {@link ResponseModel} with a {@link ResponseStatus#REPORT_EXISTS} if user already saved
     * a report today or {@link ResponseStatus#REPORT_EXPIRED} if user tried to edit a report that expired,
     * {@link ResponseStatus#INVALID_TOKEN} otherwise.
     */
    SAVE_REPORT,

    /**
     * That command close socket connection.
     */
    CLOSE
}
