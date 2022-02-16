package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class Verified {
    @SerializedName("verified")
    public boolean verified;

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
