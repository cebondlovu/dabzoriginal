package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class EncDecData {

    @SerializedName("encryptedMessage")
    public String encryptedMessage;

    @SerializedName("key")
    public String key;

    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
