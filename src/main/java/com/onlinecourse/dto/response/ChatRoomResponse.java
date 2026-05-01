package com.onlinecourse.dto.response;

public class ChatRoomResponse {

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getTargetUserEmail() {
        return targetUserEmail;
    }

    public void setTargetUserEmail(String targetUserEmail) {
        this.targetUserEmail = targetUserEmail;
    }

    public String getTargetUserFullName() {
        return targetUserFullName;
    }

    public void setTargetUserFullName(String targetUserFullName) {
        this.targetUserFullName = targetUserFullName;
    }

    public String getTargetUserAvatar() {
        return targetUserAvatar;
    }

    public void setTargetUserAvatar(String targetUserAvatar) {
        this.targetUserAvatar = targetUserAvatar;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    private Integer roomId;
    private String targetUserEmail;
    private String targetUserFullName;
    private String targetUserAvatar;
    private String lastMessage;
    private String updatedAt;

    public ChatRoomResponse() {
    }
}
