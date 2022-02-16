package com.softcrypt.deepkeysmusic.remote.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class DabzService extends FirebaseMessagingService {

    private static final String TAG = "dabzService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getFrom();

        if(remoteMessage.getData().size() > 0)
            Log.d(TAG, "MSG:" + remoteMessage.getData());

        if(remoteMessage.getNotification() != null)
            Log.d(TAG, "MSG:" + remoteMessage.getNotification().getBody());
    }
}
