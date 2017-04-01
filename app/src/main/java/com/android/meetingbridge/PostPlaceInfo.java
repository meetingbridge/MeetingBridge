package com.android.meetingbridge;

public class PostPlaceInfo {
    private String id, name, lat, lng, address, phoneNumber;


    public PostPlaceInfo(String id, String name, String lat, String lng, String address, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public PostPlaceInfo() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
