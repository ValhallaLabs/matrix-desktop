package ua.softgroup.matrix.temppackage.model.responsemodels;

import java.util.Set;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class InitializeModel extends ResponseModel {
    private static final long serialVersionUID = 1L;

    private String token;

    private Set<ProjectModel> projectModels;

    private long idlePeriod;

    private long screenshotFrequency;

    private long checkPointFrequency;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Set<ProjectModel> getProjectModels() {
        return projectModels;
    }

    public void setProjectModels(Set<ProjectModel> projectModels) {
        this.projectModels = projectModels;
    }

    public long getIdlePeriod() {
        return idlePeriod;
    }

    public void setIdlePeriod(long idlePeriod) {
        this.idlePeriod = idlePeriod;
    }

    public long getScreenshotFrequency() {
        return screenshotFrequency;
    }

    public void setScreenshotFrequency(long screenshotFrequency) {
        this.screenshotFrequency = screenshotFrequency;
    }

    public long getCheckPointFrequency() {
        return checkPointFrequency;
    }

    public void setCheckPointFrequency(long checkPointFrequency) {
        this.checkPointFrequency = checkPointFrequency;
    }
}
