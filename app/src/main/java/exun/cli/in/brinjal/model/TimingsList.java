package exun.cli.in.brinjal.model;

/**
 * Created by n00b on 3/9/2016.
 */
public class TimingsList {

    String startTime, breakStart, breakEnd, endTime, day;
    private boolean open;

    public TimingsList(){

    }

    public TimingsList(String startTime, String breakStart, String breakEnd, String endTime, String day, Boolean open){
        this.day = day;
        this.startTime = startTime;
        this.breakEnd = breakEnd;
        this.breakStart = breakStart;
        this.endTime = endTime;
        this.open = open;
    }

    public String getDay() {
        return day;
    }

    public String getBreakEnd() {
        return breakEnd;
    }

    public String getBreakStart() {
        return breakStart;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setIsOpen(boolean open) {
        this.open = open;
    }

    public void setBreakEnd(String breakEnd) {
        this.breakEnd = breakEnd;
    }

    public void setBreakStart(String breakStart) {
        this.breakStart = breakStart;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean isOpen() {
        return open;
    }
}
