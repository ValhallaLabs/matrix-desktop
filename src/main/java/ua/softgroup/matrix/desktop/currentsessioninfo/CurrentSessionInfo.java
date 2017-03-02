package ua.softgroup.matrix.desktop.currentsessioninfo;

import ua.softgroup.matrix.server.desktop.model.datamodels.InitializeModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.datamodels.SynchronizationModel;

import java.util.Set;

/**
 * Created by Vadim on 10.02.2017.
 */
// TODO package structure
public class CurrentSessionInfo {
    private static InitializeModel initializeModel = new InitializeModel();
    private static SynchronizationModel synchronizationModel = null;
    private static long projectId;

//    public static InitializeModel getInitializeModel() {
//        return initializeModel;
//    }

    public static void setInitializeModel(InitializeModel initializeModel) {
        CurrentSessionInfo.initializeModel = initializeModel;
    }

    public static String getToken() {
        return initializeModel.getToken();
    }

//    public void setToken(String token) {
//        initializeModel.setToken(token);
//    }

    public static Set<ProjectModel> getProjectModels() {
        return initializeModel.getProjectModels();
    }

//    public void setProjectModels(Set<ProjectModel> projectModels) {
//        initializeModel.setProjectModels(projectModels);
//    }

    public static long getIdlePeriod() {
        return initializeModel.getIdlePeriod();
    }

//    public void setIdlePeriod(long idlePeriod) {
//        initializeModel.setIdlePeriod(idlePeriod);
//    }

    public static long getScreenshotFrequency() {
        return initializeModel.getScreenshotFrequency();
    }

//    public void setScreenshotFrequency(long screenshotFrequency) {
//        initializeModel.setScreenshotFrequency(screenshotFrequency);
//    }

    public static long getCheckPointFrequency() {
        return initializeModel.getCheckPointFrequency();
    }

//    public void setCheckPointFrequency(long checkPointFrequency) {
//        initializeModel.setCheckPointFrequency(checkPointFrequency);
//    }

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
}
