package com.softcrypt.deepkeysmusic.viewModels;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class HomeViewModel extends AndroidViewModel {

    private final DabzDatabaseRepository dabzDatabaseRepository;
    private final DabzRepository dabzRepository;
    private final CompositeDisposable homeDisposable = new CompositeDisposable();
    private MutableLiveData<DataSnapshot> postResult = new MutableLiveData<>();
    private MutableLiveData<List<Post>> morePostResult = new MutableLiveData<>();
    private MutableLiveData<DataSnapshot> postMediaUrlResult = new MutableLiveData<>();
    private MutableLiveData<DataSnapshot> userInfoResult = new MutableLiveData<>();
    private MutableLiveData<DataSnapshot> likedResult = new MutableLiveData<>();
    private MutableLiveData<DataSnapshot> commentsResult = new MutableLiveData<>();
    private Set<Post> loadedPosts = new HashSet<>();

    public HomeViewModel(@NonNull BaseApplication application, Realm realm) {
        super(application);
        this.dabzDatabaseRepository = new DabzDatabaseRepository(realm, application, Runnable::run);
        this.dabzRepository = new DabzRepository();
    }

    public void dispose(){
        homeDisposable.clear();
        homeDisposable.dispose();
    }

    public LiveData<DataSnapshot> getProfileResult(String userId) {
        getProfile(userId);
        return userInfoResult;
    }

    public LiveData<List<Post>> getPostLiveData() {
        return morePostResult;
    }

    public Set<Post> getLoadedPosts() {
        return loadedPosts;
    }

    public LiveData<DataSnapshot> isLikedResult(String postId) {
        isLiked(postId);
        return likedResult;
    }

    public LiveData<DataSnapshot> theresCommentsResult(String postId) {
        theresComments(postId);
        return commentsResult;
    }

    public void likePost(String postId, String userId) {
        like(postId, userId);
    }

    public void unlike(String postId, String userId) {
        unlikePost(postId, userId);
    }

    public void sendNotification(String userId, HashMap<String , Object> map) {
        notification(userId, map);
    }

    private void notification(String userId, HashMap<String, Object> map) {
        homeDisposable.add(dabzRepository.sendNotification(userId, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }));
    }

    private void theresComments(String postId) {
        homeDisposable.add(dabzRepository.getComments(postId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot dataSnapshot) throws Exception {
                commentsResult.setValue(dataSnapshot);
            }
        }));
    }


    private void like(String postId, String userId) {
        homeDisposable.add(dabzRepository.likePost(postId, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }));
    }

    private void unlikePost(String postId, String userId) {
        dabzRepository.unlikePost(postId, userId);
    }

    private void isLiked(String postId) {
        homeDisposable.add(dabzRepository.isLiked(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataSnapshot>() {
                    @Override
                    public void accept(DataSnapshot snapshot) throws Exception {
                        likedResult.setValue(snapshot);
                    }
                }));
    }

    private void getProfile(String userId) {
        homeDisposable.add(dabzRepository.getProfile(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot dataSnapshot) throws Exception {
                userInfoResult.setValue(dataSnapshot);
            }
        }, throwable -> {

        }));
    }

    public boolean getPosts(int page, int pageSize) {
        AtomicBoolean isDone = new AtomicBoolean(false);
        homeDisposable.add(dabzRepository.getMorePosts(page, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataSnapshot>() {
                    @Override
                    public void accept(DataSnapshot dataSnapshot) throws Exception {
                        List<Post> posts = new ArrayList<>();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post post = snapshot.getValue(Post.class);
                            if(!loadedPosts.contains(post)) {
                                posts.add(post);
                                loadedPosts.add(post);
                            }
                        }

                        morePostResult.setValue(posts);
                        homeDisposable.clear();
                        isDone.set(true);
                    }
                }));

        return isDone.get();
    }


    private void reportError(String message) {

    }


}
