package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Post {

    @SerializedName("postID")
    private String postId;

    @SerializedName("authorId")
    private String authorId;

    @SerializedName("caption")
    private String caption;

    @SerializedName("media")
    private String media;

    @SerializedName("mediaType")
    private String mediaType;

    @SerializedName("type")
    private String type;

    @SerializedName("dateTime")
    private long dateTime;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
