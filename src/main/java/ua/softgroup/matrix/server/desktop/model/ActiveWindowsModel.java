package ua.softgroup.matrix.server.desktop.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class ActiveWindowsModel extends TokenModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long projectId;
    private Map<String, Long> windowTimeMap = new LinkedHashMap<>();

    public ActiveWindowsModel() {
    }

    public ActiveWindowsModel(Long projectId, Map<String, Long> windowTimeMap) {
        this.projectId = projectId;
        this.windowTimeMap = windowTimeMap;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Map<String, Long> getWindowTimeMap() {
        return windowTimeMap;
    }

    public void setWindowTimeMap(Map<String, Long> windowTimeMap) {
        this.windowTimeMap = windowTimeMap;
    }

    @Override
    public String toString() {
        return "ActiveWindowsModel{" +
                "windowTimeMap=" + windowTimeMap +
                '}';
    }
}
