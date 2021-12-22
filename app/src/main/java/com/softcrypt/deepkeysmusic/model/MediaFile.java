package com.softcrypt.deepkeysmusic.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class MediaFile {

    @SerializedName("reference")
    public String reference;

    @SerializedName("uri")
    public Uri uri;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
