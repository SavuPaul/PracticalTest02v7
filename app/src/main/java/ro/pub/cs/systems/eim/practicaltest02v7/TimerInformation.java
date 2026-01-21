package ro.pub.cs.systems.eim.practicaltest02v7;

public class TimerInformation {
    private String hour;
    private String minute;

    // Constructor
    public TimerInformation(String hour, String minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }
}