package ua.softgroup.matrix.server.desktop.model;

import java.io.Serializable;
import java.time.LocalDate;

public class ProjectModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

    private String title;

    private String description;

    private String authorName;

    private LocalDate startDate;

    private LocalDate endDate;

    private int rate;

    private String rateCurrency;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getRateCurrency() {
        return rateCurrency;
    }

    public void setRateCurrency(String rateCurrency) {
        this.rateCurrency = rateCurrency;
    }

    @Override
    public String toString() {
        return "ProjectModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", authorName='" + authorName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", rate=" + rate +
                ", rateCurrency=" + rateCurrency +
                '}';
    }
}
