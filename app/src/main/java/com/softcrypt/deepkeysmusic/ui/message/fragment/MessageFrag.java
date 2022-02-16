package com.softcrypt.deepkeysmusic.ui.message.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.softcrypt.deepkeysmusic.ui.contacts.ContactsAct;
import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.adapter.FavouriteMessageAdapter;
import com.softcrypt.deepkeysmusic.adapter.LatestMessageAdapter;
import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.model.LatestMessage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MessageFrag extends Fragment {

    ImageView contactsImg, menuImg, toggleImg;
    RecyclerView recyclerViewMessages, recyclerViewFavourites;
    TextView titleTxt, noMessageTxt;
    LinearLayout containerFavourites;

    List<LatestMessage> latestMessageList;
    List<LatestMessage> favouriteMessageList;

    LatestMessageAdapter latestMessageAdapter;
    FavouriteMessageAdapter favouriteMessageAdapter;

    static String tag = Common.closed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);

        contactsImg =v.findViewById(R.id.contacts_mf_img);
        contactsImg.setOnClickListener(this::navigateLinked);

        menuImg = v.findViewById(R.id.menu_mf_img);
        menuImg.setOnClickListener(this::openMenu);

        toggleImg = v.findViewById(R.id.toggle_mf_img);
        toggleImg.setOnClickListener(this::toggleMenu);
        toggleImg.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
        toggleImg.setTag(tag);

        titleTxt = v.findViewById(R.id.title_mf_txt);
        titleTxt.setOnClickListener(this::toggleMenu);
        titleTxt.setTag(tag);
        containerFavourites = v.findViewById(R.id.favorites_container_mf_lay);

        getLatestMessages();

        return v;
    }

    private void getLatestMessages() {
        //get latest messages
    }

    private void toggleMenu(View view) {
        if(titleTxt.getTag().equals(Common.closed) || toggleImg.getTag().equals(Common.closed)) {
            containerFavourites.setVisibility(View.VISIBLE);
            tag = Common.opened;
            titleTxt.setTag(tag);
            toggleImg.setTag(tag);
            toggleImg.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
        } else if(titleTxt.getTag().equals(Common.opened) || toggleImg.getTag().equals(Common.opened)) {
            containerFavourites.setVisibility(View.GONE);
            tag = Common.closed;
            titleTxt.setTag(tag);
            toggleImg.setTag(tag);
            toggleImg.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
        }
    }

    private void navigateLinked(View view) {
        startActivity(new Intent(getContext(), ContactsAct.class));
    }

    private void openMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.message_options, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::menuClick);

        try {
            Field fieldPopup = PopupMenu.class.getDeclaredField("popup");
            Object popup = fieldPopup.get(popupMenu);
            popup.getClass()
                    .getDeclaredMethod("setForceShowIcon", Boolean.class)
                    .invoke(popup, true);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            popupMenu.show();
        }
    }

    private boolean menuClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_search:
                Toast.makeText(getContext(), "Nav Search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_lock:
                checkIfLocked();

                break;
            case R.id.nav_settings:
                Toast.makeText(getContext(), "Nav info", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void checkIfLocked() {
        Toast.makeText(getContext(), "Nav locked", Toast.LENGTH_SHORT).show();
    }


}