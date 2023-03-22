package com.softcrypt.deepkeysmusic.ui.comment;

import static java.util.Observable.*;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.ColorStateList;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.hendraanggrian.appcompat.widget.SocialEditText;
import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.adapter.CommentAdapter;
import com.softcrypt.deepkeysmusic.adapter.PostAdapter;
import com.softcrypt.deepkeysmusic.base.BaseActivity;
import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.common.DisplayableError;
import com.softcrypt.deepkeysmusic.common.NavigationTypes;
import com.softcrypt.deepkeysmusic.common.ParseData;
import com.softcrypt.deepkeysmusic.model.Comment;
import com.softcrypt.deepkeysmusic.model.Post;
import com.softcrypt.deepkeysmusic.model.PostLocal;
import com.softcrypt.deepkeysmusic.tools.InternetConnected;
import com.softcrypt.deepkeysmusic.viewModels.CommentViewModel;
import com.softcrypt.deepkeysmusic.viewModels.MainViewModel;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class CommentAct extends BaseActivity implements LoaderManager.LoaderCallbacks<Boolean> {

    ImageView closeImg, sendImg, emojiImg, deleteRecordingImg;
    private ImageView recordImg;
    CircleImageView authorImg;
    RecyclerView commentsRecycler;
    LinearLayout recordingContainer;
    TextView noCommentsTxt, recordingProgressTxt;
    SocialAutoCompleteTextView commentEdt;
    ProgressBar progress, recordingProgress;
    CommentAdapter commentAdapter;
    LinearLayoutManager mLayoutManager;
    List<Comment> commentList;
    String TAG = "FB_COMMENTS", postId, authorId;
    FirebaseUser firebaseUser;
    boolean result;
    private static final int LDR_BASIC_ID = 2;
    DisplayableError displayableError;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    File asd;
    CountDownTimer cdTimer;
    private String commentStr = null;

    private int mTotalItemCount = 0;
    private int mFirstVisibleItemPosition;
    private int mVisibleItemCount;
    private boolean mIsLoading = false, mIsLastPage = false;
    private int mCommentsPerPage = 10, count = 0, mPageSize = 1;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    public static CommentViewModel commentViewModel;

    Handler handler;


    @Override
    protected void onStart() {
        super.onStart();
        requestPermission("All");
/*        Intent intent = getIntent();
        postId = intent.getStringExtra(ParseData.$POST_ID);
        authorId = intent.getStringExtra(ParseData.$AUTHOR_ID);*/
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        displayableError = DisplayableError.getInstance(this);

        commentViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(CommentViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ((BaseApplication)getApplication()).getAppComponent().injectCommentAct(this);
        commentViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(CommentViewModel.class);
        recordingContainer = findViewById(R.id.recording_container_c);

        Intent intent = getIntent();
        postId = intent.getStringExtra(ParseData.$POST_ID);
        authorId = intent.getStringExtra(ParseData.$AUTHOR_ID);

        deleteRecordingImg = findViewById(R.id.delete_recording_c_img);
        deleteRecordingImg.setOnClickListener(this::deleteComment);

        recordingProgressTxt = findViewById(R.id.recording_progress_c_txt);

        recordingProgress = findViewById(R.id.record_progress_c_pb);

        recordImg = findViewById(R.id.record_uc_img);
        recordImg.setImageResource(R.drawable.ic_baseline_mic_24);
        recordImg.setTag("record");
        recordImg.setOnClickListener(this::recordVn);

        emojiImg = findViewById(R.id.emoji_uc_img);

        progress = findViewById(R.id.progress_c_pb);

        sendImg = findViewById(R.id.post_c_img);
        sendImg.setOnClickListener(this::postComment);

        closeImg = findViewById(R.id.back_c_img);
        closeImg.setOnClickListener(this::close);

        commentList = new ArrayList<>();

        commentsRecycler = findViewById(R.id.recycler_view_c);
        mLayoutManager = new LinearLayoutManager(this);
        commentsRecycler.setLayoutManager(mLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(commentsRecycler.getContext(),
                mLayoutManager.getOrientation());
        commentsRecycler.addItemDecoration(dividerItemDecoration);

        commentAdapter = new CommentAdapter(commentViewModel, this, this);
        commentsRecycler.setAdapter(commentAdapter);

/*        commentViewModel.getCommentLiveData().observe(this, comments -> {
            Collections.sort(comments, new Comparator<Comment>() {
                @Override
                public int compare(Comment comment, Comment t1) {
                    return Long.compare(t1.getDateTime(), comment.getDateTime());
                }
            });
            commentAdapter.clear();
            commentAdapter.addAll(comments);
            mIsLoading = false;
            //swipeRefreshLayout.setRefreshing(false);
        });*/

        noCommentsTxt = findViewById(R.id.no_comments_c_txt);

        commentEdt = findViewById(R.id.comment_c_edt);
        commentEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(recordImg.getTag().equals("recording")) {
                    displayableError.createToastMessage("Stopped Recording", 1);
                    recordImg.setImageResource(R.drawable.ic_baseline_mic_24);
                    recordImg.setTag("record");
                }
                recordImg.setVisibility(View.GONE);
                emojiImg.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() < 1) {
                    recordImg.setVisibility(View.VISIBLE);
                    emojiImg.setVisibility(View.GONE);
                }
            }
        });

        commentsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //handler.postDelayed(getViewRunnable(), 5000);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        //handler.removeCallbacks(runnable);
                        //storyNested.setVisibility(View.GONE);
                        Log.d("FB_ART", "DRAGGING");//12
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        Log.d("FB_ART", "Settling");//12
                        break;

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mVisibleItemCount = mLayoutManager.getChildCount();
                mTotalItemCount = mLayoutManager.getItemCount();
                mFirstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                displayableError.createToastMessage(""+(mTotalItemCount), 1);

                if(!mIsLoading && !mIsLastPage) {
                    if ((mVisibleItemCount + mFirstVisibleItemPosition) >= mTotalItemCount
                            && mFirstVisibleItemPosition >= 0 && mTotalItemCount >= mPageSize) {
                        mPageSize++;
                        //if(commentViewModel.theresComments(postId)) {
                            //getComments();
                        //}
                    }
                }
            }
        });

        getLoaderManager().initLoader(LDR_BASIC_ID, Bundle.EMPTY, this);

        //if(commentViewModel.theresComments(postId)) {
            //getComments();
        //}
    }

    private void getComments() {
        //swipeRefreshLayout.setRefreshing(true);
        mIsLoading = true;
        if(commentViewModel.getComments(postId,mCommentsPerPage, mPageSize)) {
            //swipeRefreshLayout.setRefreshing(false);
            mIsLoading = false;
        }
    }

    private void deleteComment(View view) {
        if (deleteRecordingImg.getTag().equals("stop")) {
            if(recordImg.getTag().equals("recording")) {
                mediaRecorder.stop();
                cdTimer.cancel();
            }

            if(recordImg.getTag().equals("play")) {
                mediaPlayer.stop();
                cdTimer.cancel();
            }

            recordImg.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            recordImg.setTag("play");
            deleteRecordingImg.setImageResource(R.drawable.ic_baseline_delete_24);
            deleteRecordingImg.setTag("delete");
        } else {
            asd.delete();
            recordImg.setImageResource(R.drawable.ic_baseline_mic_24);
            recordImg.setTag("record");
            recordingContainer.setVisibility(View.GONE);
            commentEdt.setVisibility(View.VISIBLE);
        }
    }

    private void postComment(View view) {
        if(recordImg.getTag().equals("recording")) {
            mediaRecorder.stop();
            cdTimer.cancel();

            recordImg.setImageResource(R.drawable.ic_baseline_mic_24);
            recordImg.setTag("record");
            recordingContainer.setVisibility(View.GONE);
            commentEdt.setVisibility(View.VISIBLE);
        }

        if(recordImg.getTag().equals("play")) {
            mediaPlayer.stop();
            cdTimer.cancel();

            recordImg.setImageResource(R.drawable.ic_baseline_mic_24);
            recordImg.setTag("record");
            recordingContainer.setVisibility(View.GONE);
            commentEdt.setVisibility(View.VISIBLE);
        }

        if(commentEdt.length() > 0) {
            //commentEdt.setText("");
        }

        createComment();
    }

    private void createComment() {
        Comment comment = new Comment();
        comment.setCommentId(null);
        comment.setComment(commentEdt.getText().toString());
        comment.setCommentType(NavigationTypes.$TEXT);
        comment.setPublisherId(firebaseUser.getUid());
        comment.setDateTime(System.currentTimeMillis());

        commentViewModel.commentStatus(comment, postId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void recordVn(View view) {
        if (recordImg.getTag().equals("recording")) {
            mediaRecorder.stop();
            recordImg.setImageResource(R.drawable.ic_baseline_mic_24);
            recordImg.setTag("record");
            recordingContainer.setVisibility(View.GONE);
            commentEdt.setVisibility(View.VISIBLE);
        } else if (recordImg.getTag().equals("record")) {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                asd = new File(getExternalFilesDir("Dabz"),File.separator+"Comments/.Recordings/"+UUID.randomUUID().toString()+".3gp");
            else
                asd = new File(Environment.getExternalStorageDirectory(), File.separator+"Dabz/Comments/.Recordings/"+UUID.randomUUID().toString()+".3gp");
            try {
                asd.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(asd);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            try {
                recordingProgress.setMax(180);
                mediaRecorder.prepare();
                mediaRecorder.start();
                showAnimation(recordingProgress);
            } catch (IOException e) {
                e.printStackTrace();
            }

            recordImg.setImageResource(R.drawable.ic_baseline_mic_red__24);
            recordImg.setTag("recording");
            deleteRecordingImg.setImageResource(R.drawable.ic_baseline_stop_24);
            deleteRecordingImg.setTag("stop");
            recordingContainer.setVisibility(View.VISIBLE);
            commentEdt.setVisibility(View.GONE);
        } else {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(String.valueOf(asd));
                mediaPlayer.prepare();
                mediaPlayer.start();
                showPlayAnimation(recordingProgress);
            } catch (IOException e) {
                e.printStackTrace();
            }
            deleteRecordingImg.setImageResource(R.drawable.ic_baseline_stop_24);
            deleteRecordingImg.setTag("stop");
        }
    }

    private void showPlayAnimation(ProgressBar rProgress) {
        rProgress.setMax(mediaPlayer.getDuration()/1000);
        cdTimer = new CountDownTimer((mediaPlayer.getDuration()), 1000) {
            int ctr = 0;
            @Override
            public void onTick(long millisUntilFinished) {
                int c = ++ctr;
                rProgress.setProgress(c);
                recordingProgressTxt.setText(DateUtils.formatElapsedTime(c));
            }

            @Override
            public void onFinish() {
                if (cdTimer != null) {
                    if(recordImg.getTag().equals("play"))
                        mediaPlayer.stop();
                    deleteRecordingImg.setImageResource(R.drawable.ic_baseline_delete_24);
                    deleteRecordingImg.setTag("delete");
                }

            }
        };

        cdTimer.start();
    }

    private void showAnimation(ProgressBar rProgress) {
        cdTimer = new CountDownTimer(180000,1000) {
            Random random = new Random();
            int freq = random.nextInt(6);
            int ctr = 0;
            @Override
            public void onTick(long millisUntilFinished) {
                createStatisticalGraph(6,6-freq, recordingProgressTxt);
                int c = ++ctr;
                rProgress.setProgress(c);
                recordingProgressTxt.setText(DateUtils.formatElapsedTime(c));
            }

            @Override
            public void onFinish() {
                if (cdTimer != null) {
                    displayableError.createToastMessage("reached limit", 1);
                    if(recordImg.getTag().equals("recording"))
                        mediaRecorder.stop();
                    recordImg.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    recordImg.setTag("play");
                    deleteRecordingImg.setImageResource(R.drawable.ic_baseline_delete_24);
                    deleteRecordingImg.setTag("delete");
                    cdTimer.cancel();
                }
            }
        };
        cdTimer.start();
    }

    private void createStatisticalGraph(int fMax, int frequency, TextView statusBar) {

    }


    private void initUI() {
        if (result) {
            if (commentAdapter.getItemCount() < 1) {
                noCommentsTxt.setVisibility(View.VISIBLE);
                commentsRecycler.setVisibility(View.INVISIBLE);
            } else {
                noCommentsTxt.setVisibility(View.GONE);
                commentsRecycler.setVisibility(View.VISIBLE);
            }
        } else {
            noCommentsTxt.setVisibility(View.VISIBLE);
            noCommentsTxt.setText("No Internet Connection\n____");
            commentsRecycler.setVisibility(View.INVISIBLE);
        }
    }

    private void close(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(recordImg.getTag().equals("recording"))
            mediaRecorder.stop();
        finish();
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        progress.setVisibility(View.VISIBLE);
        return new InternetConnected(this);
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        result = data;
        progress.setVisibility(View.GONE);
        initUI();
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }
}