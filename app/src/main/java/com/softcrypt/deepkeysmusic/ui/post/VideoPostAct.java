package com.softcrypt.deepkeysmusic.ui.post;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.softcrypt.deepkeysmusic.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

public class VideoPostAct extends AppCompatActivity {

    VideoView mediaView;
    ImageView selectVideoImg, redoImg, undoImg, closeImg;
    TextView postTxt;
    SocialAutoCompleteTextView captionEdt;
    Uri videoUri;
    private static final int CAMERA_REQUEST = 58;
    private static final int PICK_REQUEST = 59;
    private static final int VIDEO_PICK_REQUEST = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_post);

        mediaView = findViewById(R.id.post_vp_vid);

        selectVideoImg = findViewById(R.id.gallery_vp_img);
        selectVideoImg.setOnClickListener(this::selectVideo);

        closeImg = findViewById(R.id.close_vp_img);
        closeImg.setOnClickListener(this::close);

        postTxt = findViewById(R.id.post_vp_txt);
        postTxt.setOnClickListener(this::postVideo);

        captionEdt = findViewById(R.id.caption_vp_edt);
    }

    private void postVideo(View view) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == VIDEO_PICK_REQUEST) {
                assert data != null;
                videoUri = data.getData();
                setVideoView();
            } else if(requestCode == CAMERA_REQUEST) {
                assert data != null;
                videoUri = data.getData();
                setVideoView();
            }
        } else Toast.makeText(this, "Oops Something's Wrong", Toast.LENGTH_LONG).show();
    }

    private void setVideoView() {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mediaView);

        mediaView.setMediaController(mediaController);
        mediaView.setVideoURI(videoUri);
        mediaView.requestFocus();
        mediaView.setOnPreparedListener(this::preparedListener);
    }

    private void preparedListener(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaView.pause();;
            }
        });
    }

    private void close(View view) {
        onBackPressed();
    }

    private void selectVideo(View view) {
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {
                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            startActivityForResult(intent, CAMERA_REQUEST);
                        } else if(which == 1) {
                            Intent intent = new Intent();
                            intent.setType("video/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_PICK_REQUEST);
                        }
                    }
                }).show();
    }
}