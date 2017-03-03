package ua.softgroup.matrix.server.desktop.model.datamodels;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class CheckPointModel implements Serializable, DataModel {
    private static final long serialVersionUID = 1L;

    private long order; // just an order of checkpoint

    private byte[] screenshot;

    private String keyboardLogs;

    private double mouseFootage;

    private Map<String, Long> windowsTimeMap;

    private long idleTime; //in seconds

    public CheckPointModel(long order, byte[] screenshot, String keyboardLogs, double mouseFootage, Map<String, Long> windowsTimeMap, long idleTime) {
        this.order = order;
        this.screenshot = screenshot;
        this.keyboardLogs = keyboardLogs;
        this.mouseFootage = mouseFootage;
        this.windowsTimeMap = windowsTimeMap;
        this.idleTime = idleTime;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
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

    public double getMouseFootage() {
        return mouseFootage;
    }

    public void setMouseFootage(double mouseFootage) {
        this.mouseFootage = mouseFootage;
    }

    public Map<String, Long> getWindowsTimeMap() {
        return windowsTimeMap;
    }

    public void setWindowsTimeMap(Map<String, Long> windowsTimeMap) {
        this.windowsTimeMap = windowsTimeMap;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(long idleTime) {
        this.idleTime = idleTime;
    }
}
