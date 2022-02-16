package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class HashTagItem {

    @SerializedName("postId")
    public String postId;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
