package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class HashTag {

    @SerializedName("authorId")
    public String authorId;

    @SerializedName("date")
    public long date;

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
