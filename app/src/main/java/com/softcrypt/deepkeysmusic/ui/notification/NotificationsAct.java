package com.softcrypt.deepkeysmusic.ui.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.adapter.NotificationAdapter;
import com.softcrypt.deepkeysmusic.model.Notification;

import java.util.List;

public class NotificationsAct extends AppCompatActivity {

    ImageView backImg;
    RecyclerView recyclerView;
    NotificationAdapter notificationAdapter;
    List<Notification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        backImg = findViewById(R.id.back_n_img);
        backImg.setOnClickListener(this::closeAct);
        recyclerView = findViewById(R.id.recycler_view_n_notifications);
    }

    private void closeAct(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}