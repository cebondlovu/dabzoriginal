package com.softcrypt.deepkeysmusic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.softcrypt.deepkeysmusic.model.Authentication;
import com.softcrypt.deepkeysmusic.model.DecEnc;
import com.softcrypt.deepkeysmusic.repository.DabzRepository;
import com.softcrypt.deepkeysmusic.tools.encryptionTools.CryptA;

import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends ViewModel {

    private final DabzRepository dabzRepository;
    private final CompositeDisposable loginDisposable = new CompositeDisposable();
    private final MutableLiveData<String> loginResult = new MutableLiveData<>();
    private final MutableLiveData<DataSnapshot> authResult = new MutableLiveData<>();
    private final MutableLiveData<String> decResult = new MutableLiveData<>();

    @Inject
    public LoginViewModel (DabzRepository dabzRepository) {
        this.dabzRepository = dabzRepository;
    }

    public void dispose() {
        loginDisposable.clear();
        loginDisposable.dispose();
    }

    public LiveData<String> getResult(Authentication authentication) {
        signIn(authentication);
        return loginResult;
    }

    public LiveData<DataSnapshot> getAuthResult(String email) {
        authenticationCheck(email);
        return authResult;
    }

    public LiveData<String> getDecryptedResults(DecEnc decEnc) {
        decryptData(decEnc);
        return decResult;
    }

    private void signIn(Authentication authentication) {
        loginDisposable.add(dabzRepository.signInUser(authentication)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<AuthResult>() {
            @Override
            public void accept(AuthResult authResult) throws Exception {
                loginResult.setValue(Objects.requireNonNull(authResult.getUser()).getUid());
            }
        }, throwable -> {
            reportError(throwable.getMessage());
            loginResult.setValue(null);
        }));
    }

    private void authenticationCheck(String email) {
        loginDisposable.add(dabzRepository.getRequestedUserKeys(email)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot dataSnapshot) throws Exception {
                authResult.setValue(dataSnapshot);
            }
        }, throwable -> {
            reportError(throwable.getMessage());
            authResult.setValue(null);
        }));
    }

    private void decryptData(DecEnc decEnc) {
        decResult.setValue(CryptA.decData(decEnc));
    }

    private void reportError(String message) {

    }
}
