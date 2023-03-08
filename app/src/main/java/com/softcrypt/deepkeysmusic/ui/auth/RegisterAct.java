package com.softcrypt.deepkeysmusic.ui.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.common.DisplayableError;
import com.softcrypt.deepkeysmusic.common.ErrorFactory;
import com.softcrypt.deepkeysmusic.model.Authentication;
import com.softcrypt.deepkeysmusic.model.DecEnc;
import com.softcrypt.deepkeysmusic.ui.profile.SetupProfileAct;
import com.softcrypt.deepkeysmusic.viewModels.RegisterViewModel;

import java.util.Objects;

import javax.inject.Inject;

public class RegisterAct extends AppCompatActivity {
    AppCompatEditText emailEdt, passwordEdt, confirmEdt;
    AppCompatButton registerBtn;
    AppCompatTextView altTxt, copyrightTxt;
    ProgressBar progressBar;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    RegisterViewModel registerViewModel;

    DisplayableError displayableError;

    @Override
    protected void onStart() {
        super.onStart();
        registerViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(RegisterViewModel.class);
        displayableError = DisplayableError.getInstance(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerViewModel.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ((BaseApplication)getApplication()).getAppComponent().injectRegisterAct(this);

        emailEdt = findViewById(R.id.username_r_edt);
        emailEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail(s.toString().toLowerCase());
            }
        });

        passwordEdt = findViewById(R.id.password_r_edt);
        confirmEdt = findViewById(R.id.confirm_password_r_edt);

        registerBtn = findViewById(R.id.register_r_btn);
        registerBtn.setOnClickListener(this::registerUser);

        altTxt = findViewById(R.id.alt_r_txt);
        altTxt.setOnClickListener(this::navigateLogin);

        progressBar = findViewById(R.id.loading_r_pb);

        copyrightTxt = findViewById(R.id.copyright_r_txt);

        setCopyright();
    }

    private void validateEmail(String email) {
        registerViewModel.validateEmail(email).observe(this, aBoolean -> {
            if(!aBoolean)
                emailEdt.setError(ErrorFactory.throwableMessage(2, email));
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setCopyright() {
        copyrightTxt.setText(Common.updateCopyRight());
    }

    private void registerUser(View view) {
        progressBar.setVisibility(View.VISIBLE);
        if (Objects.requireNonNull(emailEdt.getText()).toString().isEmpty() ||
                Objects.requireNonNull(passwordEdt.getText()).toString().isEmpty() ||
                Objects.requireNonNull(confirmEdt.getText()).toString().isEmpty()) {
            progressBar.setVisibility(View.GONE);
            if (Objects.requireNonNull(emailEdt.getText()).toString().isEmpty())
                emailEdt.setError(ErrorFactory.throwableMessage(1, Common.EMAIL));
            if (Objects.requireNonNull(passwordEdt.getText()).toString().isEmpty())
                passwordEdt.setError(ErrorFactory.throwableMessage(1, Common.PASSWORD));
            if (Objects.requireNonNull(confirmEdt.getText()).toString().isEmpty())
                confirmEdt.setError(ErrorFactory.throwableMessage(9,""));
        } else if (passwordEdt.getText().toString().equals(confirmEdt.getText().toString())) {
            //if(!isTest())
            registerViewModel.getEncryptedResult(passwordEdt.getText().toString()).observe(
                    this, decEnc -> {
                        if (decEnc.token != null && !decEnc.token.isEmpty()) {
                            registerViewModel.getResult(new Authentication(
                                    emailEdt.getText().toString(), decEnc.token)).observe(
                                    this, result -> {
                                        if (!result.isEmpty()) {
                                            String mail = emailEdt.getText().toString().replaceAll("[.@]*", "");
                                            storeEncryptedUserData(mail, decEnc);
                                        } else showRegisterUserError();
                                    });
                        } else showEncryptionError();
                    });
            //else navigateSetupProfile(); //Remove Test Case
        } else {
            progressBar.setVisibility(View.GONE);
            passwordEdt.setError(ErrorFactory.throwableMessage(4, Common.PASSWORDS));
            confirmEdt.setError(ErrorFactory.throwableMessage(4, Common.PASSWORDS));
        }
    }

    private void storeEncryptedUserData(String mail, DecEnc decEnc) {
        registerViewModel.getStoredEncryptedDataResult(mail, decEnc).observe(this, aBoolean -> {
            if (aBoolean)
                navigateSetupProfile();
            else showStoreEncryptedUserDataError();
        });
    }

    private void showEncryptionError() {
        displayableError.createToastMessage(ErrorFactory.throwableMessage(7, "Encryption"), 0);
    }

    private void showStoreEncryptedUserDataError() {
        displayableError.createToastMessage(ErrorFactory.throwableMessage(7, "Authentication"), 0);
    }

    private void showRegisterUserError() {
        displayableError.createToastMessage(ErrorFactory.throwableMessage(10, "Registration"), 0);
    }

    private void navigateSetupProfile() {
        displayableError.createToastMessage("Registered", 1);
        Intent intent = new Intent(this, SetupProfileAct.class);
        intent.putExtra(Common.EMAIL, Objects.requireNonNull(emailEdt.getText()).toString().toLowerCase());
        startActivity(intent);
        finishAffinity();
    }

    public void navigateLogin(View view) {
        startActivity(new Intent(this, LoginAct.class));
        finish();
    }

    private boolean isTest() {
        return true;
    }
}