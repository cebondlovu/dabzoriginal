package com.softcrypt.deepkeysmusic.viewModels;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.loader.content.AsyncTaskLoader;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.UploadTask;
import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.common.DataPaths;
import com.softcrypt.deepkeysmusic.model.MediaUrl;
import com.softcrypt.deepkeysmusic.model.Post;
import com.softcrypt.deepkeysmusic.model.PostLocal;
import com.softcrypt.deepkeysmusic.repository.DabzDatabaseRepository;
import com.softcrypt.deepkeysmusic.repository.DabzRepository;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainViewModel extends ViewModel {

    private final DabzRepository dabzRepository;
    private final DabzDatabaseRepository dabzDatabaseRepository;
    private final CompositeDisposable baseDisposable = new CompositeDisposable();
    private final MutableLiveData<DataSnapshot> authResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> postSuccessResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> postMediaUrlSuccessResult = new MutableLiveData<>();
    private MutableLiveData<String> downloadUrl = new MutableLiveData<>();
    private MutableLiveData<Boolean> postDeleteResult = new MutableLiveData<>();
    private MutableLiveData<RealmResults<PostLocal>> pendingPosts = new MutableLiveData<>();
    private MutableLiveData<Boolean> deletePendingPostResult = new MutableLiveData<>();

    @Inject
    public MainViewModel(DabzRepository dabzRepository) {
        this.dabzRepository = dabzRepository;
        this.dabzDatabaseRepository = new DabzDatabaseRepository(Realm.getDefaultInstance(),
                new BaseApplication(), Runnable::run);
    }

    public void dispose() {
        baseDisposable.clear();
        baseDisposable.dispose();
    }

    public LiveData<Boolean> getCreatePostResult(PostLocal postLocal) {
        uploadImage(postLocal);
        return postSuccessResult;
    }

    public LiveData<Boolean> getDeletePendingPostResult(String postId) {
        deletePendingPost(postId);
        return deletePendingPostResult;
    }

    public LiveData<RealmResults<PostLocal>> getPendingPostsResult() {
        getPendingPosts();
        return pendingPosts;
    }

    public LiveData<DataSnapshot> getAuthResult(String email) {
        authenticationCheck(email);
        return authResult;
    }

    private void authenticationCheck(String email) {
        baseDisposable.add(dabzRepository.getRequestedUserKeys(email)
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

    private void getPendingPosts() {
        pendingPosts.setValue(dabzDatabaseRepository.getPendingPosts());
    }

    private void deletePendingPost(String postId) {
       deletePendingPostResult.setValue(dabzDatabaseRepository.deletePendingPost(postId));
    }

    private void createPost(PostLocal postLocal, String url, String mediaType) {
        Post post = new Post();
        post.setPostId(postLocal.getPostId());
        post.setAuthorId(postLocal.getAuthorId());
        post.setCaption(postLocal.getCaption());
        post.setMedia(url);
        post.setMediaType(mediaType);
        post.setType(Common.POST);
        post.setDateTime(Long.parseLong(postLocal.getDateTime()));
        baseDisposable.add(dabzRepository.createPost(post)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        postSuccessResult.setValue(true);
                    }
                }, throwable -> {
                    postSuccessResult.setValue(false);
                    reportError(throwable.getMessage());
                }));
    }

    private void uploadImage(PostLocal postLocal) {
        String reference = "";

        if (postLocal.getType().equals(Common.IMAGE)) {
            reference = DataPaths.$IMAGE_POST_PATH + postLocal.getFileName();
        }
        if (postLocal.getType().equals(Common.VIDEO)) {
            reference = DataPaths.$VIDEO_POST_PATH + postLocal.getFileName();
        }

        String finalReference = reference;
        baseDisposable.add(dabzRepository.uploadMedia(reference, Uri.parse(postLocal.getUrl()))
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new DisposableSingleObserver<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(finalReference,postLocal, postLocal.getType());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        reportError(e.getMessage());
                        postSuccessResult.setValue(false);
                    }
                }));
    }

    private void getDownloadUrl(String reference, PostLocal postLocal, String mediaType) {
        baseDisposable.add(dabzRepository.getUrl(reference)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Uri>() {
                    @Override
                    public void accept(Uri uri) throws Exception {
                        if(uri != null) {
                            createPost(postLocal, uri.toString(),mediaType);
                        }
                    }
                }, throwable -> {
                    reportError(throwable.getMessage());
                    postSuccessResult.setValue(false);
                }));
    }

    private void addPostMedia(String postId,MediaUrl mediaUrl) {
        baseDisposable.add(dabzRepository.addPostMedia(postId, mediaUrl)
                .subscribeOn(Schedulers.io())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }, throwable -> {
                    postSuccessResult.setValue(false);
                    reportError(throwable.getMessage());
                }));
    }

    private void reportError(String message) {

    }
}
