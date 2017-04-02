package com.android.meetingbridge;

import com.google.android.gms.location.places.Place;

public class LocationInfo extends userInfo {

    private Place dest;

    public LocationInfo() {
    }

    public LocationInfo(userInfo u, Place dest) {
        super(u);
        this.dest = dest;
    }

    public Place getDest() {
        return dest;
    }

    public void setDest(Place dest) {
        this.dest = dest;
    }
}
