package com.android.meetingbridge;

public class ChatInfo {

    private String messageId, message, PostingTime;
    private userInfo host;
    private GroupInfo currentGroup;

    public ChatInfo() {
    }

    public ChatInfo(String messageId, String message, String postingTime, userInfo host, GroupInfo currentGroup) {
        this.messageId = messageId;
        this.message = message;
        PostingTime = postingTime;
        this.host = host;
        this.currentGroup = currentGroup;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPostingTime() {
        return PostingTime;
    }

    public void setPostingTime(String postingTime) {
        PostingTime = postingTime;
    }

    public userInfo getHost() {
        return host;
    }

    public void setHost(userInfo host) {
        this.host = host;
    }

    public GroupInfo getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(GroupInfo currentGroup) {
        this.currentGroup = currentGroup;
    }
}
