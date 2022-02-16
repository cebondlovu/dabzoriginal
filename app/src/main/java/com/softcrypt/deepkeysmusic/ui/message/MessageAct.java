package com.softcrypt.deepkeysmusic.ui.message;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.adapter.MessageAdapter;

public class MessageAct extends AppCompatActivity {

    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
    }
}