package ua.softgroup.matrix.temppackage.model.requestmodels;

import java.io.Serializable;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class RequestModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String token;
    private long projectId;

    public RequestModel(String token, long projectId) {
        this.token = token;
        this.projectId = projectId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }
}
