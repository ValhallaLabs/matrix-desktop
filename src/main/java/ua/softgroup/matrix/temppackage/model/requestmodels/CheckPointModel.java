package ua.softgroup.matrix.temppackage.model.requestmodels;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class CheckPointModel extends RequestModel {
    private static final long serialVersionUID = 1L;
    private byte[] screenshot;
    private String keyboardLogs;
    private long mouseFootage;
    private Map<String, Long> windowsTimeMap;

    public CheckPointModel(String token, long projectId, String keyboardLogs, long mouseFootage, Map<String, Long> windowsTimeMap) {
        super(token, projectId);
        this.keyboardLogs = keyboardLogs;
        this.mouseFootage = mouseFootage;
        this.windowsTimeMap = windowsTimeMap;
    }

    public CheckPointModel(String token, long projectId, byte[] screenshot, String keyboardLogs, long mouseFootage, Map<String, Long> windowsTimeMap) {
        super(token, projectId);
        this.screenshot = screenshot;
        this.keyboardLogs = keyboardLogs;
        this.mouseFootage = mouseFootage;
        this.windowsTimeMap = windowsTimeMap;
    }

    public byte[] getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(byte[] screenshot) {
        this.screenshot = screenshot;
    }

    public String getKeyboardLogs() {
        return keyboardLogs;
    }

    public void setKeyboardLogs(String keyboardLogs) {
        this.keyboardLogs = keyboardLogs;
    }

    public long getMouseFootage() {
        return mouseFootage;
    }

    public void setMouseFootage(long mouseFootage) {
        this.mouseFootage = mouseFootage;
    }

    public Map<String, Long> getWindowsTimeMap() {
        return windowsTimeMap;
    }

    public void setWindowsTimeMap(Map<String, Long> windowsTimeMap) {
        this.windowsTimeMap = windowsTimeMap;
    }
}
