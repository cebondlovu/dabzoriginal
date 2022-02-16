package com.softcrypt.deepkeysmusic.viewModels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.UploadTask;
import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.model.Post;
import com.softcrypt.deepkeysmusic.repository.DabzDatabaseRepository;
import com.softcrypt.deepkeysmusic.repository.DabzRepository;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class ProfileUserViewModel extends AndroidViewModel {

    private final DabzRepository dabzRepository;
    private final CompositeDisposable profileUserDisposable = new CompositeDisposable();
    private final MutableLiveData<DataSnapshot> profileResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> confirmProfileUpdateResult = new MutableLiveData<>();
    private final MutableLiveData<String> downloadUrl = new MutableLiveData<>();
    private final MutableLiveData<DataSnapshot> isVerifiedResult = new MutableLiveData<>();
    private final MutableLiveData<DataSnapshot> followersResult = new MutableLiveData<>();
    private final MutableLiveData<DataSnapshot> followingResult = new MutableLiveData<>();
    private final MutableLiveData<DataSnapshot> postResult = new MutableLiveData<>();


    public ProfileUserViewModel(@NonNull BaseApplication application) {
        super(application);
        this.dabzRepository = new DabzRepository();
    }

    public void dispose() {
        profileUserDisposable.clear();
        profileUserDisposable.dispose();
    }

    public LiveData<Boolean> getUpdateProfileResult(String userId, HashMap<String , Object> map) {
        updateProfile(userId, map);
        return confirmProfileUpdateResult;
    }

    public LiveData<String> getUploadResult(String reference, Uri uri) {
        uploadImage(reference, uri);
        return downloadUrl;
    }

    public LiveData<DataSnapshot> getProfileResult(String userId) {
        getProfile(userId);
        return profileResult;
    }

    public LiveData<DataSnapshot> getProfileVerified(String userId) {
        isProfileVerified(userId);
        return isVerifiedResult;
    }

    public LiveData<DataSnapshot> getFollowingResult(String userId) {
        getFollowing(userId);
        return followingResult;
    }

    public LiveData<DataSnapshot> getFollowersResult(String userId) {
        getFollowers(userId);
        return followersResult;
    }

    public LiveData<DataSnapshot> getUserPostsResult(String userId) {
        getUserPosts(userId);
        return postResult;
    }

    private void getUserPosts(String userId) {
        profileUserDisposable.add(dabzRepository.getUserPosts(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot dataSnapshot) throws Exception {
                postResult.setValue(dataSnapshot);
            }
        }, throwable -> {
            postResult.setValue(null);
            reportError(throwable.getMessage());
        }));
    }

    private void getFollowers(String userId) {
        profileUserDisposable.add(dabzRepository.getFollowers(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot dataSnapshot) throws Exception {
                followersResult.setValue(dataSnapshot);
            }
        }, throwable -> {
            followersResult.setValue(null);
            reportError(throwable.getMessage());
        }));
    }

    private void getFollowing(String userId) {
        profileUserDisposable.add(dabzRepository.getFollowing(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot dataSnapshot) throws Exception {
                followingResult.setValue(dataSnapshot);
            }
        }, throwable -> {
            followingResult.setValue(null);
            reportError(throwable.getMessage());
        }));
    }

    private void isProfileVerified(String userId) {
        profileUserDisposable.add(dabzRepository.isProfileVerified(userId)
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
        profileUserDisposable.add(dabzRepository.getProfile(userId)
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

    private void updateProfile(String userId, HashMap<String, Object> map) {
        profileUserDisposable.add(dabzRepository.updateUserProfile(userId, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        confirmProfileUpdateResult.setValue(true);
                    }
                }, throwable -> {
                    confirmProfileUpdateResult.setValue(false);
                    reportError(throwable.getMessage());
                }));
    }

    private void uploadImage(String reference, Uri uri) {
        profileUserDisposable.add(dabzRepository.uploadMedia(reference, uri)
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
        profileUserDisposable.add(dabzRepository.getUrl(reference)
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
