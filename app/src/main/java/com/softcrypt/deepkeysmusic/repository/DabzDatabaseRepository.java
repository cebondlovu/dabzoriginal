package com.softcrypt.deepkeysmusic.repository;

import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.database.DabzDatabaseHelper;
import com.softcrypt.deepkeysmusic.model.PostLocal;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

public class DabzDatabaseRepository {

    private DabzDatabaseHelper dabzDatabaseHelper;
    private final Realm realm;
    private final BaseApplication context;
    private final Executor executor;

    @Inject
    public DabzDatabaseRepository(Realm realm, BaseApplication context, Executor executor) {
        this.dabzDatabaseHelper = new DabzDatabaseHelper(context, realm);
        this.realm = realm;
        this.context = context;
        this.executor = executor;
    }

    public void insertPendingPost (PostLocal post, DabzRepositoryCallback<Boolean> callBack) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean result = dabzDatabaseHelper.insertPendingPost(post);
                    callBack.onComplete(result);
                } catch (Exception e) {
                    callBack.onComplete(false);
                }
            }
        });
    }


    public RealmResults<PostLocal> getPendingPosts() {
        return dabzDatabaseHelper.getPendingPosts();
    }

    public boolean deletePendingPost(String postId) {
        return dabzDatabaseHelper.deletePendingPost(postId);
    }

}
