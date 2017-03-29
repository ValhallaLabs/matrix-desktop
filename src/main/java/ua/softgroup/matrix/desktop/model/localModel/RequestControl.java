package ua.softgroup.matrix.desktop.model.localModel;

import ua.softgroup.matrix.desktop.model.DayJson;
import ua.softgroup.matrix.desktop.model.WorkPeriod;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class RequestControl implements Serializable {
    private int totalWorkSeconds;
    private int totalIdleSeconds;
    private double totalIdlePercentage;
    private long projectId;
    private String date;
    private String start;
    private String end;
    private int workSeconds;
    private int idleSeconds;
    private double idlePercentage;
    private boolean checked;
    private long checkerId;
    private double coefficient = 1.0f;
    private String reportText = "";
    private int rate;
    private int currencyId;
    private Set<WorkPeriod> workPeriod;

    public RequestControl(int totalWorkSeconds, int totalIdleSeconds, double totalIdlePercentage, long projectId, String date, String start, String end, int workSeconds, int idleSeconds,
                          double idlePercentage, boolean checked, long checkerId, double coefficient, String reportText, int rate, int currencyId, Set<WorkPeriod> workPeriod) {
        this.totalWorkSeconds = totalWorkSeconds;
        this.totalIdleSeconds = totalIdleSeconds;
        this.totalIdlePercentage = totalIdlePercentage;
        this.projectId = projectId;
        this.date = date;
        this.start = start;
        this.end = end;
        this.workSeconds = workSeconds;
        this.idleSeconds = idleSeconds;
        this.idlePercentage = idlePercentage;
        this.checked = checked;
        this.checkerId = checkerId;
        this.coefficient = coefficient;
        this.reportText = reportText;
        this.rate = rate;
        this.currencyId = currencyId;
        this.workPeriod = workPeriod;
    }

    public Set<WorkPeriod> getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Set<WorkPeriod> workPeriod) {
        this.workPeriod = workPeriod;
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

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getWorkSeconds() {
        return workSeconds;
    }

    public void setWorkSeconds(int workSeconds) {
        this.workSeconds = workSeconds;
    }

    public int getIdleSeconds() {
        return idleSeconds;
    }

    public void setIdleSeconds(int idleSeconds) {
        this.idleSeconds = idleSeconds;
    }

    public double getIdlePercentage() {
        return idlePercentage;
    }

    public void setIdlePercentage(double idlePercentage) {
        this.idlePercentage = idlePercentage;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public long getCheckerId() {
        return checkerId;
    }

    public void setCheckerId(long checkerId) {
        this.checkerId = checkerId;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }
}
