package com.softcrypt.deepkeysmusic.ui.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.adapter.ContactAdapter;

public class ContactsAct extends AppCompatActivity {

    ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
    }
}