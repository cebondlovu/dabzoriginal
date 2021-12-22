package com.softcrypt.deepkeysmusic.ui.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
//import androidx.loader.content.AsyncTaskLoader;
//import androidx.loader.content.Loader;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.common.DisplayableError;
import com.softcrypt.deepkeysmusic.common.ErrorFactory;
import com.softcrypt.deepkeysmusic.model.Authentication;
import com.softcrypt.deepkeysmusic.model.DecEnc;
import com.softcrypt.deepkeysmusic.tools.InternetConnected;
import com.softcrypt.deepkeysmusic.ui.MainActivity;
import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.ui.profile.SetupProfileAct;
import com.softcrypt.deepkeysmusic.viewModels.LoginViewModel;

import java.util.Objects;

import javax.inject.Inject;

public class LoginAct extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Boolean> {

    AppCompatEditText emailEdt, passwordEdt;
    AppCompatButton loginBtn;
    AppCompatTextView altTxt, copyrightTxt;
    ProgressBar progressBar;
    ConstraintLayout loginLay;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    LoginViewModel loginViewModel;

    DisplayableError displayableError;

    Boolean result = false;

    private static final int LDR_BASIC_ID = 1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginViewModel.dispose();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(LoginViewModel.class);
        displayableError = DisplayableError.getInstance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((BaseApplication)getApplication()).getAppComponent().injectLoginAct(this);
        displayableError = DisplayableError.getInstance(this);
        loginLay = findViewById(R.id.login_constraint);
        emailEdt = findViewById(R.id.username_l_edt);
        passwordEdt = findViewById(R.id.password_l_edt);

        loginBtn = findViewById(R.id.login_l_btn);
        loginBtn.setOnClickListener(this::login);
        loginBtn.setEnabled(false);

        altTxt = findViewById(R.id.alt_txt);
        altTxt.setOnClickListener(this::navigateRegister);

        progressBar = findViewById(R.id.loading_l_pb);

        copyrightTxt = findViewById(R.id.copyright_l_txt);
        setCopyright();

        getLoaderManager().initLoader(LDR_BASIC_ID, Bundle.EMPTY, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setCopyright() {
        copyrightTxt.setText(Common.updateCopyRight());
    }

    public void navigateRegister(View view) {
        startActivity(new Intent(this, RegisterAct.class));
        finish();
    }

    public void login(View view) {
        if(result) {
            progressBar.setVisibility(View.VISIBLE);
            if (Objects.requireNonNull(emailEdt.getText()).toString().isEmpty() ||
                    Objects.requireNonNull(passwordEdt.getText()).toString().isEmpty()) {
                progressBar.setVisibility(View.GONE);
                if (Objects.requireNonNull(emailEdt.getText()).toString().isEmpty())
                    emailEdt.setError(ErrorFactory.throwableMessage(1, Common.EMAIL));
                if (Objects.requireNonNull(passwordEdt.getText()).toString().isEmpty())
                    passwordEdt.setError(ErrorFactory.throwableMessage(1, Common.PASSWORD));
            } else {
                String mail = emailEdt.getText().toString().replaceAll("[.@]*", "");
                loginViewModel.getAuthResult(mail).observe(
                        this, authSnap -> {
                            if (authSnap != null) {
                                Toast.makeText(this, "Not Null", Toast.LENGTH_SHORT).show();
                                if (authSnap.exists()) {
                                    DecEnc decEnc = authSnap.getValue(DecEnc.class);
                                    Toast.makeText(this, decEnc.token, Toast.LENGTH_LONG).show();
                                    if (!decEnc.verified)
                                        navigateSetupProfile(decEnc);
                                    else navigateMainAct(decEnc);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    emailEdt.setError(ErrorFactory.throwableMessage(8, emailEdt.getText().toString()));
                                }
                            }
                        });
            }
        } else {
            showNetworkNotConnectedError();
        }
    }

    private void showNetworkNotConnectedError() {
        progressBar.setVisibility(View.GONE);
        displayableError.createToastMessage(ErrorFactory.throwableMessage(5, ""),2);
    }

    private void showAuthenticationError() {
        progressBar.setVisibility(View.GONE);
        Snackbar.make(loginLay, ErrorFactory.throwableMessage(3, "password"),
                Snackbar.LENGTH_LONG).show();
    }

    private void navigateSetupProfile(DecEnc decEnc) {
        loginViewModel.getDecryptedResults(decEnc).observe(this, decrypted -> {
            if (decrypted.equals(passwordEdt.getText().toString())) {
                loginViewModel.getResult(new Authentication(
                        emailEdt.getText().toString(), decEnc.token)).observe(
                        this, result -> {
                            if(result != null) {
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(this, SetupProfileAct.class));
                                finish();
                            }
                        });
            } else showAuthenticationError();
        });

    }

    private void navigateMainAct(DecEnc decEnc) {
        if(decEnc != null) {
            Toast.makeText(this, "DEC Not Null", Toast.LENGTH_SHORT).show();
            loginViewModel.getDecryptedResults(decEnc).observe(this, decrypted -> {
                Toast.makeText(this, decrypted, Toast.LENGTH_SHORT).show();
                if (decrypted.equals(passwordEdt.getText().toString())) {
                    loginViewModel.getResult(new Authentication(
                            emailEdt.getText().toString(), decEnc.token)).observe(
                            this, result -> {
                                if (result != null) {
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                }
                            });
                } else showAuthenticationError();
            });
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private boolean isTest() {
        return true;
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        return new InternetConnected(this);
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
/*        if(data)
            displayableError.createToastMessage("Connected", 0);
        else displayableError.createToastMessage("Connected", 1);*/

        result = data;
        loginBtn.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }
}