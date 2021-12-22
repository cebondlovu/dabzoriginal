package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("message")
    public String message;

    @SerializedName("media")
    public String media;

    @SerializedName("type")
    public String type;

    @SerializedName("read")
    public Boolean read;

    @SerializedName("messageKey")
    public String messageKey;

    @SerializedName("mediaKey")
    public String mediaKey;

    @SerializedName("date")
    public long date;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMediaKey() {
        return mediaKey;
    }

    public void setMediaKey(String mediaKey) {
        this.mediaKey = mediaKey;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
