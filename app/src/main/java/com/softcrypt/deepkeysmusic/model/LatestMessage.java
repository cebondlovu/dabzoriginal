package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class LatestMessage {

    @SerializedName("userId")
    public String userId;

    @SerializedName("message")
    public String message;

    @SerializedName("dateTime")
    public Long dateTime;

    @SerializedName("read")
    public Boolean read;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
