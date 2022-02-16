package com.softcrypt.deepkeysmusic.viewModels;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.UploadTask;
import com.softcrypt.deepkeysmusic.model.User;
import com.softcrypt.deepkeysmusic.repository.DabzRepository;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ProfileViewModel extends ViewModel {

    private final DabzRepository dabzRepository;
    private final CompositeDisposable profileDisposable = new CompositeDisposable();
    private final MutableLiveData<Boolean> result = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateResult = new MutableLiveData<>();
    private final MutableLiveData<String> downloadUrl = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateVerifiedResult = new MutableLiveData<>();
    private final MutableLiveData<DataSnapshot> profileResult = new MutableLiveData<>();
    private final MutableLiveData<DataSnapshot> isVerifiedResult = new MutableLiveData<>();

    @Inject
    public ProfileViewModel(DabzRepository dabzRepository) {
        this.dabzRepository = dabzRepository;
    }

    public void dispose() {
        profileDisposable.clear();
        profileDisposable.dispose();
    }

    public LiveData<Boolean> getCreateProfileResult(String userId, User user) {
        createProfile(userId, user);
        return result;
    }

    public LiveData<String> getUploadResult(String reference, Uri uri) {
        uploadImage(reference, uri);
        return downloadUrl;
    }

    public LiveData<Boolean> getUpdateProfileVerifiedResult(String email, HashMap<String, Object> map) {
        updateStoreEncryption(email, map);
        return updateVerifiedResult;
    }

    public LiveData<DataSnapshot> getProfileResult(String userId) {
        getProfile(userId);
        return profileResult;
    }

    public LiveData<DataSnapshot> getProfileVerified(String userId) {
        isProfileVerified(userId);
        return isVerifiedResult;
    }

    private void isProfileVerified(String userId) {
        profileDisposable.add(dabzRepository.isProfileVerified(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot dataSnapshot) throws Exception {
                isVerifiedResult.setValue(dataSnapshot);
            }
        }, throwable -> {
            isVerifiedResult.setValue(null);
            reportError(throwable.getMessage());
        }));
    }

    private void getProfile(String userId) {
        profileDisposable.add(dabzRepository.getProfile(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot dataSnapshot) throws Exception {
                profileResult.setValue(dataSnapshot);
            }
        }, throwable -> {
            profileResult.setValue(null);
            reportError(throwable.getMessage());
        }));
    }

    private void updateStoreEncryption(String email, HashMap<String, Object> map) {
        profileDisposable.add(dabzRepository.updateStoreEncryption(email, map)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action() {
            @Override
            public void run() throws Exception {
                updateVerifiedResult.setValue(true);
            }
        }, throwable -> {
            updateVerifiedResult.setValue(false);
            reportError(throwable.getMessage());
        }));
    }

    private void createProfile(String userId, User user) {
        profileDisposable.add(dabzRepository.createUserProfile(userId, user)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action() {
            @Override
            public void run() throws Exception {
                result.setValue(true);
            }
        }, throwable -> {
            result.setValue(false);
            reportError(throwable.getMessage());
        }));
    }

    private void uploadImage(String reference, Uri uri) {
        profileDisposable.add(dabzRepository.uploadMedia(reference, uri)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadUrl(reference);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                reportError(e.getMessage());
                downloadUrl.setValue(null);
            }
        }));
    }

    private void getDownloadUrl(String reference) {
        profileDisposable.add(dabzRepository.getUrl(reference)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Uri>() {
            @Override
            public void accept(Uri uri) throws Exception {
                downloadUrl.setValue(uri.toString());
            }
        }, throwable -> {
            reportError(throwable.getMessage());
            downloadUrl.setValue(null);
        }));
    }

    private void reportError(String message) {

    }

}
