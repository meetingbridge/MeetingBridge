package com.cs.meetingbridge;

//PostTime Class
public class PostTime {
    int hours, minutes;
    String ampm;


    public PostTime() {
    }

    public PostTime(int hours, int minutes, String ampm) {
        this.hours = hours;
        this.minutes = minutes;
        this.ampm = ampm;
    }

    public int getHours() {
        return this.hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getAmpm() {
        return ampm;
    }

    public void setAmpm(String ampm) {
        this.ampm = ampm;
    }
}
