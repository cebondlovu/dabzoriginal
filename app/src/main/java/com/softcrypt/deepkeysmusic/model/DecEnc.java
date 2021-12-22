package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class DecEnc {

    @SerializedName("token")
    public String token;

    @SerializedName("key")
    public String key;

    @SerializedName("verified")
    public boolean verified;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
