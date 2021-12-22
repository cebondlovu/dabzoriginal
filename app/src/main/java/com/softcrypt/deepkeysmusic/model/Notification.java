package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("id")
    public String id;

    @SerializedName("text")
    public String text;

    @SerializedName("postId")
    public String postId;

    @SerializedName("dateTime")
    public long dateTime;

    @SerializedName("type")
    public String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
