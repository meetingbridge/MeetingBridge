package com.android.meetingbridge;

public class PostPlaceInfo {
    private String id, name, latLng, address, phoneNumber;

    public PostPlaceInfo() {
    }

    public PostPlaceInfo(String id, String name, String latLng, String address, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.latLng = latLng;
        this.address = address;
        this.phoneNumber = phoneNumber;
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

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
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
