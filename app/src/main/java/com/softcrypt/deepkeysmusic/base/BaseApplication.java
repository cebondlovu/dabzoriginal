package com.softcrypt.deepkeysmusic.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.softcrypt.deepkeysmusic.common.DataPaths;
import com.softcrypt.deepkeysmusic.database.DabzDataBaseMigrations;
import com.softcrypt.deepkeysmusic.di.components.AppComponent;
import com.softcrypt.deepkeysmusic.di.components.DaggerAppComponent;
import com.softcrypt.deepkeysmusic.ui.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

public class BaseApplication extends Application {

    private AppComponent appComponent;
    private final int SCHEMA_VERSION = 4;
    private File file;
    private List<File> fileList;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.create();
        appCheck();
        initRealm(this);
        createAppFolder();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    private void appCheck() {
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
    }

    private void initRealm(Context context) {
        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name(DataPaths.$BASE_NAME)
                .schemaVersion(SCHEMA_VERSION)
                .migration(new DabzDataBaseMigrations())
                .rxFactory(new RealmObservableFactory(false))
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

    private void createAppFolder() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            fileList = new ArrayList<>();
            fileList.add(new File(getApplicationContext().getExternalFilesDir("Data"), File.separator + "Dabz/"));
            fileList.add(new File(getApplicationContext().getExternalFilesDir("Data"), File.separator + "Dabz/Comments/Recordings"));
            fileList.add(new File(getApplicationContext().getExternalFilesDir("Data"), File.separator + "Dabz/Messages/Media"));
            fileList.add(new File(getApplicationContext().getExternalFilesDir("Data"), File.separator + "Dabz/Database"));

            for (File dFile : fileList) {
                file = dFile;
                file.mkdirs();
            }

        } else {
            if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                fileList = new ArrayList<>();
                fileList.add(new File(android.os.Environment.getExternalStorageDirectory(), File.separator + "Dabz/"));
                fileList.add(new File(android.os.Environment.getExternalStorageDirectory(), File.separator + "Dabz/Comments/Recordings"));
                fileList.add(new File(android.os.Environment.getExternalStorageDirectory(), File.separator + "Dabz/Messages/Media"));


                for (File dFile : fileList) {
                    file = dFile;
                    file.mkdirs();
                }
            } else {

                fileList = new ArrayList<>();
                fileList.add(new File(getCacheDir(), File.separator + "Dabz/"));
                fileList.add(new File(getCacheDir(), File.separator + "Dabz/Comments/Recordings"));
                fileList.add(new File(getCacheDir(), File.separator + "Dabz/Messages/Media"));


                for (File dFile : fileList) {
                    file = dFile;
                    file.mkdirs();
                }
            }
        }
    }

}
