package com.cs.meetingbridge;

class userInfo {
    private String name;
    private String contactNum;
    private String gender;

    userInfo(String name, String contactNum, String gender) {
        this.contactNum = contactNum;
        this.gender = gender;
        this.name = name;
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
}
