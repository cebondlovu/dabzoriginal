package com.softcrypt.deepkeysmusic.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.common.DataPaths;
import com.softcrypt.deepkeysmusic.common.DisplayableError;
import com.softcrypt.deepkeysmusic.model.DecEnc;
import com.softcrypt.deepkeysmusic.model.MediaUrl;
import com.softcrypt.deepkeysmusic.model.Post;
import com.softcrypt.deepkeysmusic.model.PostLocal;
import com.softcrypt.deepkeysmusic.tools.threadingTools.ObserveOnce;
import com.softcrypt.deepkeysmusic.ui.comment.CommentAct;
import com.softcrypt.deepkeysmusic.ui.home.fragment.HomeFrag;
import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.ui.message.fragment.MessageFrag;
import com.softcrypt.deepkeysmusic.ui.post.EventDetailsAct;
import com.softcrypt.deepkeysmusic.ui.post.ImagePostAct;
import com.softcrypt.deepkeysmusic.ui.post.VideoPostAct;
import com.softcrypt.deepkeysmusic.ui.profile.SetupProfileAct;
import com.softcrypt.deepkeysmusic.ui.profile.fragment.ProfileFrag;
import com.softcrypt.deepkeysmusic.ui.search.fragment.SearchFrag;
import com.softcrypt.deepkeysmusic.viewModels.MainViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    private TextView uploadingTxt;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    public static MainViewModel mainViewModel;

    private static String email;
    private static int pendingCount = 0;
    private static PostLocal postLocal;
    private boolean isPostCreated;
    private HashMap<String, String> queuedList = new HashMap<>();

    private RealmChangeListener<RealmResults<PostLocal>> pendingChangeListener;
    private DisplayableError displayableError;
    private ObserveOnce observeOnce;
    File file;

    @Override
    protected void onStart() {
        super.onStart();
        mainViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(MainViewModel.class);
        displayableError = DisplayableError.getInstance(this);
        observeOnce = new ObserveOnce(this);
        file = new File(String.valueOf(Environment.getExternalStorageDirectory().mkdir()), "Dabz");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainViewModel.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((BaseApplication)getApplication()).getAppComponent().injectMainAct(this);
        mainViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(MainViewModel.class);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this::setOnItemSelectedListener);
        uploadingTxt = findViewById(R.id.uploading_txt);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFrag()).commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        pendingChangeListener = new RealmChangeListener<RealmResults<PostLocal>>() {
            @Override
            public void onChange(@NonNull RealmResults<PostLocal> postLocals) {
                pendingCount = postLocals.size();
                Runnable uploadJob = new Runnable() {
                    @Override
                    public void run() {
                        for (PostLocal postLocalData : postLocals) {
                            postLocal = new PostLocal();
                            postLocal.setId(postLocalData.getId());
                            postLocal.setPostId(postLocalData.getPostId());
                            postLocal.setAuthorId(postLocalData.getAuthorId());
                            postLocal.setCaption(postLocalData.getCaption());
                            postLocal.setFileName(postLocalData.getFileName());
                            postLocal.setType(postLocalData.getType());
                            postLocal.setUrl(postLocalData.getUrl());
                            postLocal.setDateTime(postLocalData.getDateTime());

                            if (createPosts(postLocal)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showSnackBar("Posts Created");
                                    }
                                });
                            }
                        }
                    }
                };

                uploadJob.run();

                if (pendingCount > 0) {
                    uploadingTxt.setVisibility(View.VISIBLE);
                    uploadingTxt.setText(new StringBuilder().append("Uploading ")
                            .append(pendingCount).append(" Posts"));
                } else{
                    uploadingTxt.setVisibility(View.GONE);
                    queuedList.clear();
                }
            }
        };

        isProfileSetup();
        getPendingPosts();
    }

    private boolean setOnItemSelectedListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                selectorFragment = new HomeFrag();
                break;
            case R.id.nav_search:
                selectorFragment = new SearchFrag();
                break;
            case R.id.nav_add:
                selectorFragment = null;
                startActivity(new Intent(this, VideoPostAct.class));
                break;
            case R.id.nav_message:
                selectorFragment =new MessageFrag();
                break;
            case R.id.nav_profile:
                selectorFragment = new ProfileFrag();
                break;
        }

        if (selectorFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectorFragment).commit();
        }

        return true;
    }

    private void isProfileSetup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(email == null) {
                    email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                }else {
                    assert email != null;
                    String mail = email.replaceAll("[.@]*", "");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            observeOnce.observeOnce(mainViewModel.getAuthResult(mail), new Observer<DataSnapshot>() {
                                @Override
                                public void onChanged(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            DecEnc decEnc = snapshot.getValue(DecEnc.class);
                                            assert decEnc != null;
                                            if(!decEnc.isVerified()){
                                                Intent intent = new Intent(MainActivity.this, SetupProfileAct.class);
                                                intent.putExtra(Common.EMAIL, email);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }).start();

    }

    private void getPendingPosts() {
        mainViewModel.getPendingPostsResult().observe(this, postLocals -> {
            postLocals.addChangeListener(pendingChangeListener);
            pendingCount = postLocals.size();
            Runnable uploadJob = new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    for (PostLocal postLocalData : postLocals) {
                        count++;
                        postLocal = new PostLocal();
                        postLocal.setId(postLocalData.getId());
                        postLocal.setPostId(postLocalData.getPostId());
                        postLocal.setAuthorId(postLocalData.getAuthorId());
                        postLocal.setCaption(postLocalData.getCaption());
                        postLocal.setFileName(postLocalData.getFileName());
                        postLocal.setType(postLocalData.getType());
                        postLocal.setUrl(postLocalData.getUrl());
                        postLocal.setDateTime(postLocalData.getDateTime());

                        if (createPosts(postLocal)) {
                            int finalCount = count;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showSnackBar("Posts Created");
                                    displayableError.createToastMessage("Uploading " + finalCount, 1);
                                }
                            });
                        }
                    }
                }
            };

            uploadJob.run();

            if (pendingCount > 0) {
                uploadingTxt.setVisibility(View.VISIBLE);
                uploadingTxt.setText(new StringBuilder().append("Uploading ")
                        .append(pendingCount).append(" Posts"));
            } else {
                uploadingTxt.setVisibility(View.GONE);
                queuedList.clear();
            }
        });
    }

    private boolean createPosts(PostLocal postLocalData) {

        if (queuedList.isEmpty()) {
            queuedList.put(postLocalData.getPostId(), postLocalData.getPostId());
            Log.d("FB_MAP_IE_KEY", postLocalData.getPostId());
            observeOnce.observeOnce(mainViewModel.getCreatePostResult(postLocalData), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    Log.d("FB_CP_", aBoolean.toString());
                    if(aBoolean){
                        observeOnce.observeOnce(mainViewModel.getDeletePendingPostResult(postLocalData.getPostId()), new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean aBoolean) {
                                Log.d("FB_DL_D", aBoolean.toString());
                                isPostCreated = aBoolean;
                            }
                        });
                    }
                }
            });

        } else {
            if(!queuedList.containsValue(postLocalData.getPostId())) {
                Log.d("FB_MAP_KEY", postLocalData.getPostId());
                queuedList.put(postLocalData.getPostId(), postLocalData.getPostId());
                observeOnce.observeOnce(mainViewModel.getCreatePostResult(postLocalData), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        Log.d("FB_CP_", aBoolean.toString());
                        if(aBoolean){
                            observeOnce.observeOnce(mainViewModel.getDeletePendingPostResult(postLocalData.getPostId()), new Observer<Boolean>() {
                                @Override
                                public void onChanged(Boolean aBoolean) {
                                    Log.d("FB_DL_D", aBoolean.toString());
                                    isPostCreated = aBoolean;
                                }
                            });
                        }
                    }
                });
            }
        }

        return isPostCreated;
    }

/*    private void uploadImage(String postId, String fileName,String mediaType, Uri uri) {
        String reference = "";

        if (mediaType.equals(Common.IMAGE)) {
            reference = DataPaths.$IMAGE_PATH + fileName;
        }
        if (mediaType.equals(Common.VIDEO)) {
            reference = DataPaths.$VIDEO_PATH + fileName;
        }

        observeOnce.observeOnce(mainViewModel.getUploadResult(reference, uri), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s != null || !s.isEmpty()) {
                    Log.d("FB_U_URI", s);
                    storeMedia(postId, s, mediaType);
                }
            }
        });
    }

    private void storeMedia(String postId, String result, String mediaType) {
        MediaUrl mediaUrl = new MediaUrl();
        mediaUrl.setUrl(result);
        mediaUrl.setType(mediaType);

        observeOnce.observeOnce(mainViewModel.getMediaUrlPostSuccessResult(postId, mediaUrl), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)

            }
        });
    }*/

    protected void showSnackBar(@NonNull String message) {
        View view = findViewById(R.id.fragment_container);
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
            //finish();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }


/*    public <T> void observeOnce(final LiveData<T> liveData, final Observer<T> observer) {
       liveData.observe(MainActivity.this ,new androidx.lifecycle.Observer<T>() {
           @Override
           public void onChanged(T t) {
               liveData.removeObserver(this);
               observer.onChanged(t);
           }
       });
    }*/

/*    public <String> void observeStringOnce(final LiveData<String> liveData, final Observer<String> observer) {
        liveData.observe(MainActivity.this ,new androidx.lifecycle.Observer<String>() {
            @Override
            public void onChanged(String t) {
                liveData.removeObserver(this);
                observer.onChanged(t);
            }
        });
    }*/
}