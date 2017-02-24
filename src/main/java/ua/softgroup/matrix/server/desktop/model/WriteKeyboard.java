package ua.softgroup.matrix.server.desktop.model;

import java.io.Serializable;

public class WriteKeyboard extends TokenModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String words;
    private long projectID;

    public WriteKeyboard(String token,String words, long projectID) {
        super(token);
        this.words = words;
        this.projectID = projectID;
    }

    public long getProjectID() {
        return projectID;
    }

    public void setProjectID(long projectID) {
        this.projectID = projectID;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return "WriteKeyboard{" +
                "words='" + words + '\'' +
                ", projectID=" + projectID +
                '}';
    }
}
