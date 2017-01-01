package com.cs.meetingbridge;

import java.util.ArrayList;

public class GroupInfo {
    String groupId, groupName;
    ArrayList<userInfo> membersList = new ArrayList<>();

    public GroupInfo(ArrayList<userInfo> membersList, String groupName, String groupId) {
        this.membersList = membersList;
        this.groupName = groupName;
        this.groupId = groupId;
    }

    public GroupInfo() {

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
