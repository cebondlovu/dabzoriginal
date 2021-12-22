package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class Mention {

    @SerializedName("userId")
    public String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
