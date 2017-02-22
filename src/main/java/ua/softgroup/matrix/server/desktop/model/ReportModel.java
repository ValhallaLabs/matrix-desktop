package ua.softgroup.matrix.server.desktop.model;

import java.io.Serializable;
import java.time.LocalDate;

public class ReportModel extends TokenModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

    private String title;

    private String description;

    private long projectId;

    private int status;

    private boolean checked;

    private LocalDate date;

    public ReportModel(String token) {
        super(token);
    }

    public ReportModel(String token, String description, long projectId, LocalDate date) {
        super(token);
        this.description = description;
        this.projectId = projectId;
        this.date = date;
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