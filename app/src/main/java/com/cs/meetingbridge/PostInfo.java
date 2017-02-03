package com.cs.meetingbridge;

public class PostInfo {
    private userInfo host;
    private String postId, postTitle, postDescription, postingTime;
    private PostTime postTime;
    private PostDate postDate;

    public PostInfo() {
    }

    public PostInfo(String postId, String postTitle, String postDescription, PostTime postTime, PostDate postDate, userInfo host, String postingTime) {
        this.postTitle = postTitle;
        this.postId = postId;
        this.postDescription = postDescription;
        this.postTime = postTime;
        this.postDate = postDate;
        this.host = host;
        this.postingTime = postingTime;
    }

    public String getPostingTime() {
        return postingTime;
    }

    public void setPostingTime(String postingTime) {
        this.postingTime = postingTime;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public userInfo getHost() {
        return host;
    }

    public void setHost(userInfo host) {
        this.host = host;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public PostTime getPostTime() {
        return postTime;
    }

    public void setPostTime(PostTime PostTime) {
        this.postTime = PostTime;
    }

    public PostDate getPostDate() {
        return postDate;
    }

    public void setPostDate(PostDate postDate) {
        this.postDate = postDate;
    }
}

