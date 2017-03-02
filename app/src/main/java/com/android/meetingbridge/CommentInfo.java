package com.android.meetingbridge;


public class CommentInfo {
    private userInfo host;
    private String commentTime, commentDescription;
    private GroupInfo group;

    public CommentInfo() {
    }

    public CommentInfo(String commentTime, userInfo host, String commentDescription, GroupInfo group) {
        this.commentTime = commentTime;
        this.host = host;
        this.commentDescription = commentDescription;
        this.group = group;
    }

    public CommentInfo(String currentTime, userInfo user, String commentDescription) {
        this.commentTime = currentTime;
        this.host = user;
        this.commentDescription = commentDescription;
    }

    public userInfo getHost() {
        return this.host;
    }

    public void setHost(userInfo host) {
        this.host = host;
    }

    public String getCommentTime() {
        return this.commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentDescription() {
        return commentDescription;
    }

    public void setCommentDescription(String commentDescription) {
        this.commentDescription = commentDescription;
    }

    public GroupInfo getGroup() {
        return group;
    }

    public void setGroup(GroupInfo group) {
        this.group = group;
    }
}
