package com.softcrypt.deepkeysmusic.base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.tools.InternetConnected;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void requestPermission(String permission) {
        switch (permission) {
            case "All": {
                Dexter.withActivity(this)
                        .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    if (ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                                                    PackageManager.PERMISSION_GRANTED &&
                                            ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                                    PackageManager.PERMISSION_GRANTED &&
                                            ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                                    PackageManager.PERMISSION_GRANTED &&
                                            ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.RECORD_AUDIO) !=
                                                    PackageManager.PERMISSION_GRANTED &&
                                            ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                                                    PackageManager.PERMISSION_GRANTED) {

                                    }

                                }

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                showSnackBar("Permissions Denied");
                            }
                        }).check();
                break;
            }
            case Common.$RECORD_PERMISSION : {
                Dexter.withActivity(this)
                        .withPermissions(Manifest.permission.RECORD_AUDIO)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if(report.areAllPermissionsGranted()){
                                    if(ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.RECORD_AUDIO) !=
                                            PackageManager.PERMISSION_GRANTED) {

                                    }
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                showSnackBar("Permission Denied");
                            }
                        }).check();
                break;
            }

        }
    }

    public boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    protected void showLoading(@NonNull String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    protected void showSnackBar(@NonNull String message) {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}