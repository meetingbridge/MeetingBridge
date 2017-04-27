package com.android.meetingbridge;

public class PostInfo {
    private userInfo host;
    private String postId, postTitle, postDescription, postingTime, postType;
    private PostPlaceInfo postLocation;
    private PostTime postTime;
    private PostDate postDate;
    private GroupInfo groupInfo;

    public PostInfo() {
        //Null Constructor
    }

    public PostInfo(String postId, String postType, String postTitle, String postDescription, PostPlaceInfo postLocation,
                    PostTime postTime, PostDate postDate, userInfo host, String postingTime) {
        this.host = host;
        this.postId = postId;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
        this.postingTime = postingTime;
        this.postType = postType;
        this.postLocation = postLocation;
        this.postTime = postTime;
        this.postDate = postDate;
    }

    public PostInfo(String postId, String postType, String postTitle, String postDescription, PostPlaceInfo postLocation,
                    PostTime postTime, PostDate postDate, userInfo host, String postingTime, GroupInfo groupInfo) {
        this.postTitle = postTitle;
        this.postId = postId;
        this.postType = postType;
        this.postDescription = postDescription;
        this.postTime = postTime;
        this.postDate = postDate;
        this.host = host;
        this.postLocation = postLocation;
        this.postingTime = postingTime;
        this.groupInfo = groupInfo;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public PostPlaceInfo getPostLocation() {
        return postLocation;
    }

    public void setPostLocation(PostPlaceInfo postLocation) {
        this.postLocation = postLocation;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
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

