package ua.softgroup.matrix.server.desktop.model;

public class ScreenshotModel extends TokenModel{
    private static final long serialVersionUID = 1L;
    private byte[] file;
    private long projectID;

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
