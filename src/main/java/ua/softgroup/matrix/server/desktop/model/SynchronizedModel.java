package ua.softgroup.matrix.server.desktop.model;

import java.io.Serializable;
import java.util.HashSet;

public class SynchronizedModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashSet<ReportModel> reportModel;
    private HashSet<TimeModel> timeModel;
    private HashSet<TimeModel> downtimeModel;

    public HashSet<ReportModel> getReportModel() {
        return reportModel;
    }

    public void setReportModel(HashSet<ReportModel> reportModel) {
        this.reportModel = reportModel;
    }

    public HashSet<TimeModel> getTimeModel() {
        return timeModel;
    }

    public void setTimeModel(HashSet<TimeModel> timeModel) {
        this.timeModel = timeModel;
    }

    public HashSet<TimeModel> getDowntimeModel() {
        return downtimeModel;
    }

    public void setDowntimeModel(HashSet<TimeModel> downtimeModel) {
        this.downtimeModel = downtimeModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SynchronizedModel that = (SynchronizedModel) o;
        if (reportModel != null ? !reportModel.equals(that.reportModel) : that.reportModel != null) {
            return false;
        }
        if (timeModel != null ? !timeModel.equals(that.timeModel) : that.timeModel != null) {
            return false;
        }
        return downtimeModel != null ? downtimeModel.equals(that.downtimeModel) : that.downtimeModel == null;
    }

    @Override
    public int hashCode() {
        int result = reportModel != null ? reportModel.hashCode() : 0;
        result = 31 * result + (timeModel != null ? timeModel.hashCode() : 0);
        result = 31 * result + (downtimeModel != null ? downtimeModel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SynchronizedModel{" +
                "reportModel=" + reportModel +
                ", timeModel=" + timeModel +
                ", downtimeModel=" + downtimeModel +
                '}';
    }
}
