package com.softcrypt.deepkeysmusic.ui.post;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.common.DisplayableError;

public class EventDetailsAct extends AppCompatActivity {


    //Page 484
    TextView finishImg;
    DisplayableError displayableError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        finishImg = findViewById(R.id.finish_ep_txt);
        finishImg.setOnClickListener(this::openDialog);
        displayableError = DisplayableError.getInstance(this);
    }

    private void openDialog(View view) {
        // get alert_dialog.xml view
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.dialog_add_ticket, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getApplicationContext(), R.style.AlertDialog_AppCompat);
// set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        //final EditText userInput = (EditText) promptsView.findViewById(R.id.etUserInput);
        final Button doneBtn = promptsView.findViewById(R.id.done_btn_ad);
        final Button cancelBtn = promptsView.findViewById(R.id.cancel_btn_ad);

        AlertDialog alertDialog = alertDialogBuilder.create();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayableError.createToastMessage("Done Clicked", 1);
            }
        });

        alertDialog.show();
    }
}