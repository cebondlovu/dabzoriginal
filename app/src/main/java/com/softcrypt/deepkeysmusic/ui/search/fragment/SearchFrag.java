package com.softcrypt.deepkeysmusic.ui.search.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.adapter.HashTagAdapter;
import com.softcrypt.deepkeysmusic.adapter.SuggestionAdapter;
import com.softcrypt.deepkeysmusic.adapter.UserAdapter;
import com.softcrypt.deepkeysmusic.model.Tag;
import com.softcrypt.deepkeysmusic.model.User;

import java.util.List;

public class SearchFrag extends Fragment {

    UserAdapter userAdapter;
    HashTagAdapter hashTagAdapter;
    SuggestionAdapter suggestionAdapter;
    List<Tag> tagList;
    List<User> userList;
    List<User> suggestedList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
}