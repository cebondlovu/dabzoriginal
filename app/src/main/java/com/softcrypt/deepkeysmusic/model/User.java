package com.softcrypt.deepkeysmusic.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    public String id;

    @SerializedName("username")
    public String username;

    @SerializedName("name")
    public String name;

    @SerializedName("bio")
    public String bio;

    @SerializedName("email")
    public String email;

    @SerializedName("msidn")
    public String msidn;

    @SerializedName("website")
    public String website;

    @SerializedName("location")
    public String location;

    @SerializedName("timeStamp")
    public Long timeStamp;

    @SerializedName("imageUrl")
    public String imageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsidn() {
        return msidn;
    }

    public void setMsidn(String msidn) {
        this.msidn = msidn;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
