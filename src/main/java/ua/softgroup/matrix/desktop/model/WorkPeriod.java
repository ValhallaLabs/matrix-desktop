package ua.softgroup.matrix.desktop.model;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class WorkPeriod {

    private String start;
    private String end;

    public WorkPeriod() {
    }

    public WorkPeriod(String start, String end) {
        this.start = start;
        this.end = end;
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

    @Override
    public String toString() {
        return "WorkPeriod{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
