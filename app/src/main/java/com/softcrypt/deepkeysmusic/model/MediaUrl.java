package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class MediaUrl {

    @SerializedName("url")
    private String url;
    @SerializedName("type")
    private String type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
