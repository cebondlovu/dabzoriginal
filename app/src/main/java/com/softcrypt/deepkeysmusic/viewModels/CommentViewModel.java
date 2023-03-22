package com.softcrypt.deepkeysmusic.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.softcrypt.deepkeysmusic.model.Comment;
import com.softcrypt.deepkeysmusic.model.Post;
import com.softcrypt.deepkeysmusic.model.PostLocal;
import com.softcrypt.deepkeysmusic.repository.DabzRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CommentViewModel extends ViewModel {

    private final DabzRepository dabzRepository;
    private final CompositeDisposable commentDisposable = new CompositeDisposable();
    private MutableLiveData<List<Comment>> commentsResult = new MutableLiveData<>();
    private MutableLiveData<DataSnapshot> commentResult = new MutableLiveData<>();
    private MutableLiveData<DataSnapshot> replyResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> commentStatusResult =new MutableLiveData<>();
    private MutableLiveData<Boolean> replyStatusResult = new MutableLiveData<>();
    private Set<Comment> loadedComments = new HashSet<>();

    @Inject
    public CommentViewModel(DabzRepository dabzRepository) {
        this.dabzRepository = dabzRepository;
    }

    public void dispose(){
        commentDisposable.clear();
        commentDisposable.dispose();
    }

    public LiveData<List<Comment>> getCommentLiveData() {
        return commentsResult;
    }

    public LiveData<List<Comment>> getCommentResults() {
        return commentsResult;
    }

    public Set<Comment> getLoadedComments() {
        return loadedComments;
    }

    public LiveData<DataSnapshot> getReplyResults(String postId, String commentId) {
        getReplies(postId, commentId);
        return replyResult;
    }

    public LiveData<DataSnapshot> theresCommentsResult(String postId) {
        theresComments(postId);
        return commentResult;
    }

/*    public LiveData<Boolean> commentStatus(String comment,String type, String userId, String postId) {
        commentPost(comment,type,userId,postId);
        return commentStatusResult;
    }*/

    public LiveData<Boolean> commentStatus(Comment comment, String postId) {
        commentPost(comment,postId);
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

/*    private void commentPost(String comment, String type, String userId, String postId) {
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
    }*/

    private void commentPost(Comment comment, String postId) {
        commentDisposable.add(dabzRepository.createComment(comment,postId)
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

/*    private void getComments(String postId, int page, int pageSize) {
        commentDisposable.add(dabzRepository.getComments(postId, page, pageSize)
        .subscribeOn(Schedulers.newThread())
        .subscribe(new Consumer<DataSnapshot>() {
            @Override
            public void accept(DataSnapshot dataSnapshot) throws Exception {
                commentsResult.postValue(dataSnapshot);
            }
        }, throwable -> {
            commentsResult.postValue(null);
        }));
    }*/

    public boolean theresComments(String postId) {
        AtomicBoolean exists = new AtomicBoolean(false);
        commentDisposable.add(dabzRepository.checkCommentsExist(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        exists.set(aBoolean);
                    }
                }));

        return  exists.get();
    }

    public boolean getComments(String postId, int page, int pageSize) {
        AtomicBoolean isDone = new AtomicBoolean(false);
        commentDisposable.add(dabzRepository.getComments(postId, page, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataSnapshot>() {
                    @Override
                    public void accept(DataSnapshot dataSnapshot) throws Exception {
                        List<Comment> comments = new ArrayList<>();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Comment comment = snapshot.getValue(Comment.class);
                            if(!loadedComments.contains(comment)) {
                                comments.add(comment);
                                loadedComments.add(comment);
                            }
                        }

                        commentsResult.setValue(comments);
                        commentDisposable.clear();
                        isDone.set(true);
                    }
                }));

        return isDone.get();
    }
}
