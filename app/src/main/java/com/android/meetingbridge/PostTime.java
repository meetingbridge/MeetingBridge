package com.android.meetingbridge;

//postTime Class
public class PostTime {
    String hours, minutes;
    String ampm;


    public PostTime() {
    }

    public PostTime(String hours, String minutes, String ampm) {
        this.hours = hours;
        this.minutes = minutes;
        this.ampm = ampm;
    }

    public String getHours() {
        return this.hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getAmpm() {
        return ampm;
    }

    public void setAmpm(String ampm) {
        this.ampm = ampm;
    }
}
