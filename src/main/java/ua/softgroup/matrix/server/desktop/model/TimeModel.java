package ua.softgroup.matrix.server.desktop.model;

import java.io.Serializable;

public class TimeModel extends TokenModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private long projectId;
    private long minute;
    private long hours;
    private boolean isDownTime = false;
    private double percentDownTime;

    public TimeModel(long hours, long minute) {
        this.hours = hours;
        this.minute = minute;
    }

    public TimeModel(long hours, long minute, double percentDownTime) {
        this.hours = hours;
        this.minute = minute;
        this.percentDownTime = percentDownTime;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getMinute() {
        return minute;
    }

    public void setMinute(long minute) {
        this.minute = minute;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public boolean isDownTime() {
        return isDownTime;
    }

    public void setDownTime(boolean downTime) {
        isDownTime = downTime;
    }

    public double getPercentDownTime() {
        return percentDownTime;
    }

    public void setPercentDownTime(double percentDownTime) {
        this.percentDownTime = percentDownTime;
    }

    @Override
    public String toString() {
        return "TimeModel{" +
                "projectId=" + projectId +
                ", minute=" + minute +
                ", hours=" + hours +
                ", isDownTime=" + isDownTime +
                ", percentDownTime=" + percentDownTime +
                '}';
    }
}
