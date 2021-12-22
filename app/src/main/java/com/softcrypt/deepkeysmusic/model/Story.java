package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class Story {

    @SerializedName("storyID")
    public String storyId;

    @SerializedName("authorId")
    public String authorId;

    @SerializedName("mediaUrl")
    public String mediaUrl;

    @SerializedName("caption")
    public String caption;

    @SerializedName("dateTime")
    public long dateTime;

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
