package com.android.meetingbridge;

class userInfo {
    private String name;
    private String id;
    private String contactNum;
    private String gender;
    private String email;
    private String imageUri;

    userInfo(String id, String name, String contactNum, String gender, String email, String imageUri) {
        this.contactNum = contactNum;
        this.gender = gender;
        this.name = name;
        this.email = email;
        this.id = id;
        this.imageUri = imageUri;
    }

    public userInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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