package ua.softgroup.matrix.server.desktop.model.datamodels;

import java.io.Serializable;

/**
 * @author Vadim Boitsov <sg.vadimbojcov@gmail.com>
 */
public class TimeModel implements Serializable, DataModel {
    private static final long serialVersionUID = 1L;

    private long totalTime; //in minutes

    private long todayTime; //in minutes

    private double idlePercent;

    public TimeModel(long totalTime, long todayTime, double idlePercent) {
        this.totalTime = totalTime;
        this.todayTime = todayTime;
        this.idlePercent = idlePercent;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getTodayTime() {
        return todayTime;
    }

    public void setTodayTime(long todayTime) {
        this.todayTime = todayTime;
    }

    public double getIdlePercent() {
        return idlePercent;
    }

    public void setIdlePercent(double idlePercent) {
        this.idlePercent = idlePercent;
    }
}
