package ua.softgroup.matrix.desktop.model;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ReportControlModel implements Serializable {
    private String date;
    private int totalWorkSeconds;
    private int totalIdleSeconds;
    private double totalIdlePercentage;
    private Set<DayJson> workDays;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotalWorkSeconds() {
        return totalWorkSeconds;
    }

    public void setTotalWorkSeconds(int totalWorkSeconds) {
        this.totalWorkSeconds = totalWorkSeconds;
    }

    public int getTotalIdleSeconds() {
        return totalIdleSeconds;
    }

    public void setTotalIdleSeconds(int totalIdleSeconds) {
        this.totalIdleSeconds = totalIdleSeconds;
    }

    public double getTotalIdlePercentage() {
        return totalIdlePercentage;
    }

    public void setTotalIdlePercentage(double totalIdlePercentage) {
        this.totalIdlePercentage = totalIdlePercentage;
    }

    public Set<DayJson> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(Set<DayJson> workDays) {
        this.workDays = workDays;
    }

    @Override
    public String toString() {
        return "ReportControlModel{" +
                "date='" + date + '\'' +
                ", totalWorkSeconds=" + totalWorkSeconds +
                ", totalIdleSeconds=" + totalIdleSeconds +
                ", totalIdlePercentage=" + totalIdlePercentage +
                ", workDays=" + workDays +
                '}';
    }
}
