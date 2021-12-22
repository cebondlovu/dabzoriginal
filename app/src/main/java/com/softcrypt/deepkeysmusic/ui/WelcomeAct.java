package com.softcrypt.deepkeysmusic.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.base.BaseActivity;
import com.softcrypt.deepkeysmusic.base.PermissionCallback;
import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.common.DisplayableError;
import com.softcrypt.deepkeysmusic.model.Comment;
import com.softcrypt.deepkeysmusic.ui.auth.LoginAct;
import com.softcrypt.deepkeysmusic.ui.auth.RegisterAct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WelcomeAct extends BaseActivity {

    ImageView logoImg;
    AppCompatTextView copyrightTxt;
    AppCompatButton loginBtn, registerBtn;
    DisplayableError displayableError;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onStart() {
        super.onStart();
        displayableError = DisplayableError.getInstance(this);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        requestPermission("All");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loginBtn = findViewById(R.id.login_w_btn);
        loginBtn.setOnClickListener(this::loginAct);

        registerBtn = findViewById(R.id.register_w_btn);
        registerBtn.setOnClickListener(this::registerAct);

        copyrightTxt = findViewById(R.id.copyright_w_txt);
        setCopyright();
    }

    private void setCopyright() {
        copyrightTxt.setText(Common.updateWelcomeCopyRight());
    }

    public void registerAct(View view) {
        startActivity(new Intent(this, RegisterAct.class));
    }

    public void loginAct(View view) {
        startActivity(new Intent(this, LoginAct.class));
    }
}