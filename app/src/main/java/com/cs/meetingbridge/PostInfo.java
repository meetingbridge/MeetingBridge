package com.cs.meetingbridge;

public class PostInfo {
    int id;
    userInfo host;
    String title, description;
    PostTime PostTime;
    PostDate postDate;


    public PostInfo() {
    }

    public PostInfo(int id, String title, String description, PostTime PostTime, PostDate postDate, userInfo host) {
        this.title = title;
        this.id = id;
        this.description = description;
        this.PostTime = PostTime;
        this.postDate = postDate;
        this.host = host;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public userInfo getHost() {
        return host;
    }

    public void setHost(userInfo host) {
        this.host = host;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PostTime getPostTime() {
        return PostTime;
    }

    public void setPostTime(PostTime PostTime) {
        this.PostTime = PostTime;
    }

    public PostDate getPostDate() {
        return postDate;
    }

    public void setPostDate(PostDate postDate) {
        this.postDate = postDate;
    }
}

