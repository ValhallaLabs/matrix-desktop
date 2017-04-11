package ua.softgroup.matrix.desktop.session.current;

import ua.softgroup.matrix.api.model.datamodels.InitializeModel;
import ua.softgroup.matrix.api.model.datamodels.ProjectModel;
import ua.softgroup.matrix.api.model.datamodels.SynchronizationModel;

import java.util.List;
import java.util.Set;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class CurrentSessionInfo {
    private static InitializeModel initializeModel = new InitializeModel();
    private static SynchronizationModel synchronizationModel = null;
    private static long projectId;

    public static void setInitializeModel(InitializeModel initializeModel) {
        CurrentSessionInfo.initializeModel = initializeModel;
    }

    public static String getToken() {
        return initializeModel.getToken();
    }

    public static Set<ProjectModel> getProjectModels() {
        return initializeModel.getProjectModels();
    }

    public static int getIdlePeriod() {
        return initializeModel.getIdlePeriod();
    }

    public static int getScreenshotFrequency() {
        return initializeModel.getScreenshotFrequency();
    }

    public static int getCheckPointPeriod() {
        return initializeModel.getCheckPointPeriod();
    }

    public static SynchronizationModel getSynchronizationModel() {
        return synchronizationModel;
    }

    public static void setSynchronizationModel(SynchronizationModel synchronizationModel) {
        CurrentSessionInfo.synchronizationModel = synchronizationModel;
    }

    public static long getProjectId() {
        return projectId;
    }

    public static void setProjectId(long projectId) {
        CurrentSessionInfo.projectId = projectId;
    }

    public static List<String> getBhSet() {
        return initializeModel.getBhSet();
    }
}
