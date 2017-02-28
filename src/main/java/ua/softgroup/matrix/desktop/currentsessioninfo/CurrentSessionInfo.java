package ua.softgroup.matrix.desktop.currentsessioninfo;

import ua.softgroup.matrix.temppackage.model.responsemodels.InitializeModel;
import ua.softgroup.matrix.temppackage.model.responsemodels.ProjectModel;

import java.util.Set;

/**
 * Created by Vadim on 10.02.2017.
 */
// TODO package structure
public class CurrentSessionInfo {
    private static InitializeModel initializeModel;

//    public static InitializeModel getInitializeModel() {
//        return initializeModel;
//    }

    public static void setInitializeModel(InitializeModel initializeModel) {
        CurrentSessionInfo.initializeModel = initializeModel;
    }

    public String getToken() {
        return initializeModel.getToken();
    }

//    public void setToken(String token) {
//        initializeModel.setToken(token);
//    }

    public Set<ProjectModel> getProjectModels() {
        return initializeModel.getProjectModels();
    }

//    public void setProjectModels(Set<ProjectModel> projectModels) {
//        initializeModel.setProjectModels(projectModels);
//    }

    public long getIdlePeriod() {
        return initializeModel.getIdlePeriod();
    }

//    public void setIdlePeriod(long idlePeriod) {
//        initializeModel.setIdlePeriod(idlePeriod);
//    }

    public long getScreenshotFrequency() {
        return initializeModel.getScreenshotFrequency();
    }

//    public void setScreenshotFrequency(long screenshotFrequency) {
//        initializeModel.setScreenshotFrequency(screenshotFrequency);
//    }

    public long getCheckPointFrequency() {
        return initializeModel.getCheckPointFrequency();
    }

//    public void setCheckPointFrequency(long checkPointFrequency) {
//        initializeModel.setCheckPointFrequency(checkPointFrequency);
//    }
}
