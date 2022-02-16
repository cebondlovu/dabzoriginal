package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class Tag {

    @SerializedName("postId")
    public String postId;

    @SerializedName("tagger")
    public String tagger;

    @SerializedName("date")
    public long date;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTagger() {
        return tagger;
    }

    public void setTagger(String tagger) {
        this.tagger = tagger;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
