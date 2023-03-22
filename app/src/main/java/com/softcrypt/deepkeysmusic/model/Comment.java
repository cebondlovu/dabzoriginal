package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Comment {

    @SerializedName("commentId")
    public String commentId;

    @SerializedName("comment")
    public String comment;

    @SerializedName("commentType")
    public String commentType;

    @SerializedName("publisherId")
    public String publisherId;

    @SerializedName("dateTime")
    private long dateTime;

    @SerializedName("replies")
    public List<String> replies;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public List<String> getReplies() {
        return replies;
    }

    public void setReplies(List<String> replies) {
        this.replies = replies;
    }
}
