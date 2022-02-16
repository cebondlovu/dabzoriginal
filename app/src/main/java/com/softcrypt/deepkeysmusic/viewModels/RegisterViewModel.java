package com.softcrypt.deepkeysmusic.viewModels;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthResult;
import com.softcrypt.deepkeysmusic.model.Authentication;
import com.softcrypt.deepkeysmusic.model.DecEnc;
import com.softcrypt.deepkeysmusic.repository.DabzRepository;
import com.softcrypt.deepkeysmusic.tools.encryptionTools.CryptA;

import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RegisterViewModel extends ViewModel {

    private final DabzRepository dabzRepository;
    private final CompositeDisposable registerDisposable = new CompositeDisposable();
    private final MutableLiveData<String> registerResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> storeResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEmailValid = new MutableLiveData<>();
    private final MutableLiveData<DecEnc> encryptedResult = new MutableLiveData<>();

    @Inject
    public RegisterViewModel(DabzRepository dabzRepository) {
        this.dabzRepository = dabzRepository;
    }

    public void dispose() {
        registerDisposable.clear();
        registerDisposable.dispose();
    }

    public LiveData<String> getResult(Authentication authentication) {
        registerWithEmailAndPassword(authentication);
        return registerResult;
    }

    public LiveData<DecEnc> getEncryptedResult(String password) {
        encryptPassword(password);
        return encryptedResult;
    }

    public LiveData<Boolean> getStoredEncryptedDataResult(String email, DecEnc decEnc) {
        storeEncryptedData(email, decEnc);
        return storeResult;
    }

    public LiveData<Boolean> validateEmail(String email) {
        checkEmailAddress(email);
        return isEmailValid;
    }

    private void checkEmailAddress(String email) {
        isEmailValid.setValue(Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private void registerWithEmailAndPassword(Authentication authentication) {
        registerDisposable.add(dabzRepository.registerUser(authentication)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<AuthResult>() {
            @Override
            public void accept(AuthResult authResult) throws Exception {
                registerResult.setValue(Objects.requireNonNull(authResult.getUser()).getUid());
            }
        }, throwable -> {
            reportError(throwable.getMessage());
        }));
    }

    private void encryptPassword(String password) {
        encryptedResult.setValue(CryptA.encData(password));
    }

    private void storeEncryptedData(String email, DecEnc decEnc) {
        registerDisposable.add(dabzRepository.storeEncryption(email, decEnc)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action() {
            @Override
            public void run() throws Exception {
                storeResult.setValue(true);
            }
        }, throwable -> {

        }));
    }

    private void reportError(String message) {

    }
}
