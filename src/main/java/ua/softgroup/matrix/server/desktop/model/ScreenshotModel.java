package ua.softgroup.matrix.server.desktop.model;

import java.io.Serializable;

public class ScreenshotModel implements Serializable{
    private static final long serialVersionUID = 1L;
    private byte[] file;
    private long projectID;

    public ScreenshotModel(byte[] file, long projectID) {
        this.file = file;
        this.projectID = projectID;
    }

    public long getProjectID() {
        return projectID;
    }

    public void setProjectID(long projectID) {
        this.projectID = projectID;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
