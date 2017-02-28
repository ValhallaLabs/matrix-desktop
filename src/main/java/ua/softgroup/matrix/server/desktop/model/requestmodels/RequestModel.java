package ua.softgroup.matrix.server.desktop.model.requestmodels;

import com.google.common.base.Optional;
import ua.softgroup.matrix.server.desktop.model.datamodels.DataModel;

import java.io.Serializable;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class RequestModel<T extends DataModel> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String token;

    private long projectId;

    private Optional<T> container;

    public RequestModel() {
    }

    public RequestModel(Optional<T> container) {
        this.container = container;
    }

    public RequestModel(String token, long projectId, Optional<T> container) {
        this.token = token;
        this.projectId = projectId;
        this.container = container;
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

    public Optional<T> getContainer() {
        return container;
    }

    public void setContainer(Optional<T> container) {
        this.container = container;
    }
}
