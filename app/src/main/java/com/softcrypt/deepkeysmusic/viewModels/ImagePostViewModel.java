package com.softcrypt.deepkeysmusic.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.common.GlobalInit;
import com.softcrypt.deepkeysmusic.model.PostLocal;
import com.softcrypt.deepkeysmusic.repository.DabzDatabaseRepository;
import com.softcrypt.deepkeysmusic.repository.DabzRepository;
import com.softcrypt.deepkeysmusic.repository.DabzRepositoryCallback;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ImagePostViewModel extends ViewModel {

    private final DabzDatabaseRepository dabzDatabaseRepository;
    private final DabzRepository dabzRepository;
    private final CompositeDisposable imagePostDisposable = new CompositeDisposable();
    private final MutableLiveData<Boolean> resultPostSuccess = new MutableLiveData<>();

    @Inject
    public ImagePostViewModel(DabzRepository dabzRepository) {
        this.dabzRepository = dabzRepository;
        this.dabzDatabaseRepository = new DabzDatabaseRepository(Realm.getDefaultInstance(),
                new BaseApplication(), Runnable::run);
    }

    public void dispose() {
        imagePostDisposable.clear();
        imagePostDisposable.dispose();
    }

    public LiveData<Boolean> insertPendingPostResult(PostLocal postLocal) {
        insertPostLocal(postLocal);
        return resultPostSuccess;
    }

    private void insertPostLocal(PostLocal postLocal) {
        dabzDatabaseRepository.insertPendingPost(postLocal, new DabzRepositoryCallback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                resultPostSuccess.setValue(result);
            }
        });
    }

/*    public LiveData<Boolean> getCreatePostResult(Post post) {
        createPost(post);
        return postSuccessResult;
    }

    public LiveData<Boolean> getMediaUrlPostSuccessResult(String postId, MediaUrl mediaUrl) {
        addPostMedia(postId, mediaUrl);
        return postMediaUrlSuccessResult;
    }

    public LiveData<String> getUploadResult(String reference, Uri uri) {
        uploadImage(reference, uri);
        return downloadUrl;
    }*/

/*    private void addPostMedia(String postId,MediaUrl mediaUrl) {
        imagePostDisposable.add(dabzRepository.addPostMedia(postId, mediaUrl)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action() {
            @Override
            public void run() throws Exception {
                //postMediaUrlSuccessResult.setValue(true);
                pmsResult = true;
            }
        }, throwable -> {
            //postMediaUrlSuccessResult.setValue(false);
            pmsResult = false;
            reportError(throwable.getMessage());
        }));
    }

    private void createPost(Post post) {
        imagePostDisposable.add(dabzRepository.createPost(post)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action() {
            @Override
            public void run() throws Exception {
                //postSuccessResult.setValue(true);
                pSuResult = true;
            }
        }, throwable -> {
            //postSuccessResult.setValue(false);
            pSuResult = false;
            reportError(throwable.getMessage());
        }));
    }

    private void uploadImage(String reference, Uri uri) {
        imagePostDisposable.add(dabzRepository.uploadMedia(reference, uri)
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
                        //downloadUrl.setValue(null);
                        dUrl = null;
                    }
                }));
    }

    private void getDownloadUrl(String reference) {
        imagePostDisposable.add(dabzRepository.getUrl(reference)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Uri>() {
                    @Override
                    public void accept(Uri uri) throws Exception {
                        //downloadUrl.setValue(uri.toString());
                        dUrl = uri.toString();
                    }
                }, throwable -> {
                    reportError(throwable.getMessage());
                    //downloadUrl.setValue(null);
                    dUrl = null;
                }));
    }*/

    private void reportError(String message) {

    }
}
