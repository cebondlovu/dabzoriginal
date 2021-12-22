package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class PinLock {

    @SerializedName("encrypted")
    public String encrypted;

    public String getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }
}
