package ua.softgroup.matrix.server.desktop.model.datamodels;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class InitializeModel implements Serializable, DataModel {
    private static final long serialVersionUID = 1L;

    private String token;

    private Set<ProjectModel> projectModels;

    private long idlePeriod;

    private long screenshotFrequency;

    private long checkPointFrequency;

    public InitializeModel() {
    }

    public InitializeModel(String token, Set<ProjectModel> projectModels, long idlePeriod, long screenshotFrequency, long checkPointFrequency) {
        this.token = token;
        this.projectModels = projectModels;
        this.idlePeriod = idlePeriod;
        this.screenshotFrequency = screenshotFrequency;
        this.checkPointFrequency = checkPointFrequency;
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

    @Override
    public String toString() {
        return "InitializeModel{" +
                "token='" + token + '\'' +
                ", projectModels=" + projectModels +
                ", idlePeriod=" + idlePeriod +
                ", screenshotFrequency=" + screenshotFrequency +
                ", checkPointFrequency=" + checkPointFrequency +
                '}';
    }
}
