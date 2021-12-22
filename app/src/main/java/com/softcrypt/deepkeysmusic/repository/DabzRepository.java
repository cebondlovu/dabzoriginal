package com.softcrypt.deepkeysmusic.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.common.DataPaths;
import com.softcrypt.deepkeysmusic.model.Authentication;
import com.softcrypt.deepkeysmusic.model.DecEnc;
import com.softcrypt.deepkeysmusic.model.MediaUrl;
import com.softcrypt.deepkeysmusic.model.Post;
import com.softcrypt.deepkeysmusic.model.PostLocal;
import com.softcrypt.deepkeysmusic.model.User;
import com.softcrypt.deepkeysmusic.remote.IDabzApi;
import com.softcrypt.deepkeysmusic.tools.firebaseTools.RxCompletableHandler;
import com.softcrypt.deepkeysmusic.tools.firebaseTools.RxFirebaseAuth;
import com.softcrypt.deepkeysmusic.tools.firebaseTools.RxFirebaseDatabase;
import com.softcrypt.deepkeysmusic.tools.firebaseTools.RxFirebaseStorage;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

public class DabzRepository {

    private final FirebaseAuth firebaseAuth;
    private IDabzApi iDabzApi;
    private final String $BASE_URL = "https://deep-keys-music-default-rtdb.europe-west1.firebasedatabase.app/";
    private final String $STORAGE_URL = "gs://deep-keys-music.appspot.com";

    @Inject
    public DabzRepository(IDabzApi iDabzApi) {
        this.iDabzApi = iDabzApi;
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public DabzRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Authentication Code.
     * Every method to do with registering and signing in
     * Add Any New Code Here, Do not edit existing code.
     */
    public Maybe<AuthResult> registerUser(Authentication authentication) {
        return RxFirebaseAuth.createUserWithEmailAndPassword(firebaseAuth, authentication.email,
                authentication.token);
    }

    public Maybe<AuthResult> signInUser(Authentication authentication) {
        return RxFirebaseAuth.signInWithEmailAndPassword(firebaseAuth, authentication.email,
                authentication.token);
    }

    public Flowable<DataSnapshot> getRequestedUserKeys(String email) {
        Query query = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$KEYS_PATH)
                .child(email);
        return RxFirebaseDatabase.observeValueEvent(query);
    }

    public Completable storeEncryption(String email,DecEnc decEnc) {
        DatabaseReference ref = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$KEYS_PATH)
                .child(email);
        return RxFirebaseDatabase.setValue(ref, decEnc);
    }

    public Completable updateStoreEncryption(String email, HashMap<String , Object> map) {
        DatabaseReference ref = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$KEYS_PATH)
                .child(email);
        return RxFirebaseDatabase.updateChildren(ref, map);
    }

    /**
     * Profile Code.
     * Every method to do with profiles
     * Add Any New Code Here, Do not edit existing code.
     * If its a query use the order by | equal to expressions
     */
    public Completable createUserProfile(String userId, User user) {
        DatabaseReference ref = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$USERS_PATH)
                .child(userId);
        return RxFirebaseDatabase.setValue(ref, user);
    }

    public Completable updateUserProfile(String userId, HashMap<String , Object> map) {
        DatabaseReference ref = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$USERS_PATH)
                .child(userId);
        return RxFirebaseDatabase.updateChildren(ref, map);
    }

    public Flowable<DataSnapshot> getProfile(String userId) {
        Query query = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$USERS_PATH)
                .child(userId);
        return RxFirebaseDatabase.observeValueEvent(query);
    }

    public Flowable<DataSnapshot> isProfileVerified(String userId) {
        Query query = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$VERIFICATION_PATH)
                .child(userId);
        return RxFirebaseDatabase.observeValueEvent(query);
    }

    /**
     * Followers & Following Code.
     * Every method to do with followers(e.g follow, block, unfollow)
     * Add Any New Code Here, Do not edit existing code.
     */
    public Completable follow(String userId, String clientId) {
        DatabaseReference ref = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$FOLLOWS_PATH)
                .child(userId)
                .child(DataPaths.$FOLLOWING_CHILD_PATH)
                .child(clientId);
        return RxFirebaseDatabase.setValue(ref, true);
    }

    public Completable createViceFollowBranch(String clientId, String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$FOLLOWS_PATH)
                .child(clientId)
                .child(DataPaths.$FOLLOWERS_CHILD_PATH)
                .child(userId);
        return RxFirebaseDatabase.setValue(ref, true);
    }

    public Completable unFollow(String userId, String clientId) {
        Task<Void> task = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$FOLLOWS_PATH)
                .child(userId)
                .child(DataPaths.$FOLLOWING_CHILD_PATH)
                .child(clientId).removeValue();
        return Completable.create(emitter -> RxCompletableHandler.assignOnTask(emitter, task));
    }

    public Completable removeViceFollowBranch(String clientId, String userId) {
        Task<Void> task = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$FOLLOWS_PATH)
                .child(clientId)
                .child(DataPaths.$FOLLOWERS_CHILD_PATH)
                .child(userId).removeValue();
        return Completable.create(emitter -> RxCompletableHandler.assignOnTask(emitter, task));
    }

    public Flowable<DataSnapshot> getFollowing(String userId) {
        Query query = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$FOLLOWS_PATH)
                .child(userId)
                .child(DataPaths.$FOLLOWING_CHILD_PATH);
        return RxFirebaseDatabase.observeValueEvent(query);
    }

    public Flowable<DataSnapshot> getFollowers(String userId) {
        Query query = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$FOLLOWS_PATH)
                .child(userId)
                .child(DataPaths.$FOLLOWERS_CHILD_PATH);
        return RxFirebaseDatabase.observeValueEvent(query);
    }

    /**
     * Post Code.
     * Every method to do with posts
     * Add Any New Code Here, Do not edit existing code.
     */
    public Completable createPost(Post postLocal) {
        DatabaseReference ref = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$POSTS_PATH)
                .push();

        final Post post = new Post();
        post.setPostId(ref.getKey());
        post.setAuthorId(postLocal.getAuthorId());
        post.setCaption(postLocal.getCaption());
        post.setMedia(postLocal.getMedia());
        post.setMediaType(postLocal.getMediaType());
        post.setType(postLocal.getType());
        post.setDateTime(postLocal.getDateTime());

        return RxFirebaseDatabase.setValue(ref, post);
    }

    public Completable deletePost(String postId) {
        Task<Void> task = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$POSTS_PATH)
                .child(postId).removeValue();
        return Completable.create(emitter -> RxCompletableHandler.assignOnTask(emitter, task));
    }

    public Completable addPostMedia(String postId, MediaUrl mediaUrl) {
        DatabaseReference ref = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$MEDIA_PATH)
                .child(postId);
        return RxFirebaseDatabase.setValue(ref, mediaUrl);
    }

    public Completable deletePostMedia(String postId) {
        Task<Void> task = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$MEDIA_PATH)
                .child(postId).removeValue();
        return Completable.create(emitter -> RxCompletableHandler.assignOnTask(emitter, task));
    }

    public Flowable<DataSnapshot> getPost(String postId) {
        Query query = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$POSTS_PATH)
                .child(postId);
        return RxFirebaseDatabase.observeValueEvent(query);
    }

    public Flowable<DataSnapshot> getUserPosts(String userId) {
        Query query = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$POSTS_PATH)
                .orderByChild(DataPaths.$AUTHOR_SUB_CHILD_PATH)
                .equalTo(userId);
        return RxFirebaseDatabase.observeValueEvent(query);
    }

    public Maybe<DataSnapshot> getPosts(int limit) {
        Log.d("FB_Q1", String.valueOf(true));
        Query query = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$POSTS_PATH)
                .orderByChild(DataPaths.$POST_ID)
                .limitToLast(limit);

        return RxFirebaseDatabase.observeSingleValueEvent(query);
    }

    public Maybe<DataSnapshot> getMorePosts(int limit, String start) {
        Log.d("FB_Q2", String.valueOf(true) + start);
        Query query = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$POSTS_PATH)
                .orderByChild(DataPaths.$POST_ID)
                .startAt(start)
                .limitToLast(limit);


        return RxFirebaseDatabase.observeSingleValueEvent(query);
    }


    public Completable likePost(String postId, String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$LIKES_PATH)
                .child(postId)
                .child(userId);
        return RxFirebaseDatabase.setValue(ref, true);
    }

    public void unlikePost(String postId, String userId){
        FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$LIKES_PATH)
                .child(postId)
                .child(userId).removeValue();
    }

    public Flowable<DataSnapshot> isLiked(String postId) {
        Query query = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$LIKES_PATH)
                .child(postId);
        return RxFirebaseDatabase.observeValueEvent(query, BackpressureStrategy.LATEST);
    }

    public Completable sendNotification(String userId, HashMap<String, Object> map) {
        DatabaseReference reference = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$NOTIFICATIONS_PATH)
                .child(userId)
                .push();

        return RxFirebaseDatabase.setValue(reference, map);
    }

    public Completable createComment(String comment, String userId, String postId) {
        HashMap<String,Object> map = new HashMap<>();

        DatabaseReference ref = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$COMMENTS_PATH)
                .child(postId);

        String id = ref.push().getKey();

        map.put("id", id);
        map.put("comment", comment);
        map.put("publisher", userId);

        return RxFirebaseDatabase.setValue(ref.child(id), map);
    }

    public Flowable<DataSnapshot> getComments(String postId) {
        Query query = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$COMMENTS_PATH)
                .child(postId);
        return RxFirebaseDatabase.observeValueEvent(query, BackpressureStrategy.LATEST);
    }

    public Completable deleteComment (String postId, String id) {
        Task<Void> task = FirebaseDatabase.getInstance($BASE_URL).getReference()
                .child(DataPaths.$COMMENTS_PATH)
                .child(postId)
                .child(id).removeValue();

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter emitter) throws Exception {
                RxCompletableHandler.assignOnTask(emitter, task);
            }
        });
    }

    /**
     * Media Code.
     * Every method to do with uploading media(e.g documents, audio, video, zipFiles)
     * Add Any New Code Here, Do not edit existing code.
     */
    public Single<UploadTask.TaskSnapshot> uploadMedia(String reference, Uri uri) {
        StorageReference ref = FirebaseStorage.getInstance().getReference(reference);
        return RxFirebaseStorage.putFile(ref, uri);
    }

    public Maybe<Uri> getUrl(String reference) {
        StorageReference ref = FirebaseStorage.getInstance($STORAGE_URL).getReference(reference);
        return RxFirebaseStorage.getDownloadUrl(ref);
    }

}
