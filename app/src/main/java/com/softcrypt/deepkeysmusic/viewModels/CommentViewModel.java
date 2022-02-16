package com.softcrypt.deepkeysmusic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.softcrypt.deepkeysmusic.repository.DabzRepository;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CommentViewModel extends ViewModel {
    private final DabzRepository dabzRepository;
    private final CompositeDisposable commentDisposable = new CompositeDisposable();
    private MutableLiveData<DataSnapshot> commentsResult = new MutableLiveData<>();
    private MutableLiveData<DataSnapshot> replyResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> commentStatusResult =new MutableLiveData<>();
    private MutableLiveData<Boolean> replyStatusResult = new MutableLiveData<>();

    @Inject
    public CommentViewModel(DabzRepository dabzRepository) {
        this.dabzRepository = dabzRepository;
    }

    public void dispose(){
        commentDisposable.clear();
        commentDisposable.dispose();
    }

    public LiveData<DataSnapshot> getCommentResults(String postId) {
        getComments(postId);
        return commentsResult;
    }

    public LiveData<DataSnapshot> getReplyResults(String postId, String commentId) {
        getReplies(postId, commentId);
        return replyResult;
    }

    public LiveData<Boolean> commentStatus(String comment,String type, String userId, String postId) {
        commentPost(comment,type,userId,postId);
        return commentStatusResult;
    }


    public LiveData<Boolean> replyStatus(String comment, String type, String userId, String postId, String commentId) {
        replyComment(comment, type, userId, postId, commentId);
        return replyStatusResult;
    }

    private void replyComment(String comment, String type, String userId, String postId, String commentId) {
        commentDisposable.add(dabzRepository.createReply(comment, type, userId, postId, commentId)
        .subscribeOn(Schedulers.newThread())
        .subscribe(new Action() {
            @Override
            public void run() throws Exception {
                replyStatusResult.postValue(true);
            }
        }, throwable -> replyStatusResult.postValue(false)));
    }

    private void commentPost(String comment, String type, String userId, String postId) {
        commentDisposable.add(dabzRepository.createComment(comment, type, userId, postId)
        .subscribeOn(Schedulers.newThread())
        .subscribe(new Action() {
            @Override
            public void run() throws Exception {
                commentStatusResult.postValue(true);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                commentStatusResult.postValue(false);
            }
        }));
    }

    private void getReplies(String postId, String commentId) {
        commentDisposable.add(dabzRepository.getReplies(postId, commentId)
        .subscribeOn(Schedulers.newThread())
        .subscribe(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot dataSnapshot) throws Exception {
                replyResult.postValue(dataSnapshot);
            }
        }, throwable -> replyResult.postValue(null)));
    }

    private void getComments(String postId) {
        commentDisposable.add(dabzRepository.getComments(postId)
        .subscribeOn(Schedulers.newThread())
        .subscribe(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot dataSnapshot) throws Exception {
                commentsResult.postValue(dataSnapshot);
            }
        }, throwable -> {
            commentsResult.postValue(null);
        }));
    }
}
