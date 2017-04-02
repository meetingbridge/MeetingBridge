package com.android.meetingbridge;

class userInfo {
    private String name;
    private String id;
    private String contactNum;
    private String gender;
    private String email;
    private double lat, lng;


    public userInfo(String id, String name, String contactNum, String gender, String email, double lat, double lng) {
        this.contactNum = contactNum;
        this.gender = gender;
        this.name = name;
        this.email = email;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    public userInfo(userInfo u) {
        this.contactNum = u.getContactNum();
        this.gender = u.getGender();
        this.name = u.getName();
        this.email = u.getEmail();
        this.id = u.getId();
        this.lat = u.getLat();
        this.lng = u.getLng();
    }

    public userInfo() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
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

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
