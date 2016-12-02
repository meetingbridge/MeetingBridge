package com.cs.meetingbridge;

class userInfo {
    private String name;
    private String contactNum;
    private String gender;
    private String email;

    userInfo(String name, String contactNum, String gender, String email) {
        this.contactNum = contactNum;
        this.gender = gender;
        this.name = name;
        this.email = email;
    }

    public userInfo() {
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
