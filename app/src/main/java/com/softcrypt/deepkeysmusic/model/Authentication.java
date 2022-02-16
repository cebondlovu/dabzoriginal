package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class Authentication {

    @SerializedName("email")
    public String email;

    @SerializedName("token")
    public String token;

    public Authentication(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
