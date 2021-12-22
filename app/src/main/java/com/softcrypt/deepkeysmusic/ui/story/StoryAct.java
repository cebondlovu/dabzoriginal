package com.softcrypt.deepkeysmusic.ui.story;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.adapter.LargeStoryAdapter;
import com.softcrypt.deepkeysmusic.model.Story;

import java.util.List;

public class StoryAct extends AppCompatActivity {

    LargeStoryAdapter largeStoryAdapter;
    List<String> followingList;
    List<Story> storyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
    }
}