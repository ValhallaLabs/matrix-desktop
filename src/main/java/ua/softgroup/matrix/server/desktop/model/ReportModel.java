package ua.softgroup.matrix.server.desktop.model;

import java.time.LocalDate;

public class ReportModel {
    private static final long serialVersionUID = 1L;

    private TokenModel tokenModel;

    private long id;

    private String title;

    private String description;

    private long projectId;

    private int status;

    private boolean checked;

    private LocalDate date;

    public ReportModel() {
    }

    public ReportModel(LocalDate date, long id, boolean checked, String description) {
        this.date = date;
        this.id = id;
        this.checked = checked;
        this.description = description;
    }

    public ReportModel(long id, TokenModel tokenModel, String title, String description, long projectId) {
        this.tokenModel = tokenModel;
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectId = projectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ReportModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", projectId=" + projectId +
                ", status=" + status +
                '}';
    }
}