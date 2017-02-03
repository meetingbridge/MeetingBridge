package com.cs.meetingbridge;

import java.util.ArrayList;

public class GroupInfo {
    String groupId, groupName;
    ArrayList<userInfo> membersList = new ArrayList<>();
    ArrayList<PostInfo> postInfos;

    public GroupInfo(String groupId, String groupName, ArrayList<userInfo> membersList, ArrayList<PostInfo> postInfos) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.membersList = membersList;
        this.postInfos = postInfos;
    }

    public GroupInfo(ArrayList<userInfo> membersList, String groupName, String groupId) {
        this.membersList = membersList;
        this.groupName = groupName;
        this.groupId = groupId;
    }

    public GroupInfo() {

    }

    public ArrayList<PostInfo> getPostInfos() {
        return postInfos;
    }

    public void setPostInfos(ArrayList<PostInfo> postInfos) {
        this.postInfos = postInfos;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<userInfo> getMembersList() {
        return membersList;
    }

    public void setMembersList(ArrayList<userInfo> membersList) {
        this.membersList = membersList;
    }
}
