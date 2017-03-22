package ua.softgroup.matrix.desktop.model;

import java.time.LocalDate;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ReportControlModel {
    private LocalDate date;
    private long projectId;
    private String startWork;
    private String endWork;
    private int workSecond;
    private int idleSecond;
    private int idlePercentage;
    private long supervisorId;
    private boolean checked;
    private double coefficient;
    private int rate;
    private int currency;
    private String  text;

    public ReportControlModel() {
    }

    public ReportControlModel(LocalDate date, long projectId, String startWork, String endWork, int workSecond, int idleSecond,
                                                                   int idlePercentage, long supervisorId, boolean checked, double coefficient, int rate, int currency, String text) {
        this.date = date;
        this.projectId = projectId;
        this.startWork = startWork;
        this.endWork = endWork;
        this.workSecond = workSecond;
        this.idleSecond = idleSecond;
        this.idlePercentage = idlePercentage;
        this.supervisorId = supervisorId;
        this.checked = checked;
        this.coefficient = coefficient;
        this.rate = rate;
        this.currency = currency;
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getStartWork() {
        return startWork;
    }

    public void setStartWork(String startWork) {
        this.startWork = startWork;
    }

    public String getEndWork() {
        return endWork;
    }

    public void setEndWork(String endWork) {
        this.endWork = endWork;
    }

    public int getWorkSecond() {
        return workSecond;
    }

    public void setWorkSecond(int workSecond) {
        this.workSecond = workSecond;
    }

    public int getIdleSecond() {
        return idleSecond;
    }

    public void setIdleSecond(int idleSecond) {
        this.idleSecond = idleSecond;
    }

    public int getIdlePercentage() {
        return idlePercentage;
    }

    public void setIdlePercentage(int idlePercentage) {
        this.idlePercentage = idlePercentage;
    }

    public long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(long supervisorId) {
        this.supervisorId = supervisorId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
