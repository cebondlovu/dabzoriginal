package com.softcrypt.deepkeysmusic.common;

import java.util.Calendar;

import io.realm.Realm;

public class Common {
    public static final String EMAIL = "Email";
    public static final String PASSWORD = "Password";
    public static final String PASSWORDS = "Passwords";
    public static final String ACCOUNT = "Account";
    public static final String USERNAME = "Username";
    public static final String NAME = "Name";
    public static final String PHONE_NUMBER = "Phone Number";
    public static final String POST = "post";
    public static final String POST_AD = "ad";
    public static final String POST_EVENT = "event";
    public static final String $LIKED = "liked";
    public static final String $RECORD_PERMISSION = "record";
    public static String selectedImage = "Selected Image";
    public static String selectImage = "Select Image";
    public static String closed = "closed";
    public static String opened = "opened";
    public static String follow = "follow";
    public static String following = "following";
    static String copyright = "Dabz © 2014 - ";
    static String owner = "From Softcrypt";

    public static final String TEXT = "Text";
    public static final String IMAGE = "Image";
    public static final String VIDEO = "Video";
    public static final String LOCATION = "Location";
    public static final String VOICE_NOTE = "VoiceNote";
    public static final String REPLY = "Reply";

    public static String updateCopyRight() {
        String copyrightString = new StringBuilder().append(copyright)
                .append(Calendar.getInstance().get(Calendar.YEAR)).toString();
        return copyrightString;
    }

    public static String updateWelcomeCopyRight() {
        String copyrightString = new StringBuilder().append("By Softcrypt")
                .append("\n").append(copyright)
                .append(Calendar.getInstance().get(Calendar.YEAR)).toString();
        return copyrightString;
    }
}
