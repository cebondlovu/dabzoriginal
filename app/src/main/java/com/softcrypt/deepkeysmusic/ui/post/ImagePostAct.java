package com.softcrypt.deepkeysmusic.ui.post;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static com.softcrypt.deepkeysmusic.tools.FileSaveHelper.isSdkHigherThan28;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.adapter.EditingToolsAdapter;
import com.softcrypt.deepkeysmusic.adapter.FilterViewAdapter;
import com.softcrypt.deepkeysmusic.adapter.listeners.FilterListener;
import com.softcrypt.deepkeysmusic.adapter.listeners.OnItemSelected;
import com.softcrypt.deepkeysmusic.adapter.tools.ToolType;
import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.common.DisplayableError;
import com.softcrypt.deepkeysmusic.common.NavigationTypes;
import com.softcrypt.deepkeysmusic.model.PostLocal;
import com.softcrypt.deepkeysmusic.tools.FileSaveHelper;
import com.softcrypt.deepkeysmusic.ui.dialogs.TextEditorDialogFragment;
import com.softcrypt.deepkeysmusic.ui.post.fragments.EmojiBSFragment;
import com.softcrypt.deepkeysmusic.ui.post.fragments.EmojiListener;
import com.softcrypt.deepkeysmusic.ui.post.fragments.Properties;
import com.softcrypt.deepkeysmusic.ui.post.fragments.PropertiesBSFragment;
import com.softcrypt.deepkeysmusic.ui.post.fragments.ShapeBSFragment;
import com.softcrypt.deepkeysmusic.ui.post.fragments.ShapeProperties;
import com.softcrypt.deepkeysmusic.ui.post.fragments.StickerBSFragment;
import com.softcrypt.deepkeysmusic.ui.post.fragments.StickerListener;
import com.softcrypt.deepkeysmusic.viewModels.ImagePostViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder;
import ja.burhanrashid52.photoeditor.shape.ShapeType;

public class ImagePostAct extends AppCompatActivity implements OnPhotoEditorListener, Properties, ShapeProperties, EmojiListener, StickerListener, OnItemSelected, FilterListener {

    private static final int LDR_BASIC_ID = 2;

    private ImageView closeImg, imgUndo, imgRedo, imgGallery, captionImg;
    private PhotoEditorView postImg;
    private TextView postTxt, mTxtCurrentTool;
    private SocialAutoCompleteTextView captionEdt;
    private RecyclerView mRvTools, mRvFilters;
    private LinearLayout captionContainer;

    @Nullable
    @VisibleForTesting
    private Uri imageUri;
    private static String type;

    private FileSaveHelper mSaveFileHelper;

    FirebaseUser firebaseUser;
    DisplayableError displayableError;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    ImagePostViewModel imagePostViewModel;

    private static final String TAG = ImagePostAct.class.getSimpleName();
    public static final String FILE_PROVIDER_AUTHORITY = "com.burhanrashid52.photoeditor.fileprovider";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    public static final String ACTION_NEXTGEN_EDIT = "action_nextgen_edit";
    public static final String PINCH_TEXT_SCALABLE_INTENT_KEY = "PINCH_TEXT_SCALABLE";


    private PropertiesBSFragment mPropertiesBSFragment;
    private ShapeBSFragment mShapeBSFragment;
    private ShapeBuilder mShapeBuilder;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private Typeface mWonderFont;
    PhotoEditor photoEditor;

    private final EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private final FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private ConstraintLayout mRootView;
    private final ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void init() {
        displayableError = DisplayableError.getInstance(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        imagePostViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(ImagePostViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_post);
        ((BaseApplication)getApplication()).getAppComponent().injectImagePostAct(this);

        initViews();
        setImagePostType();
        handleIntentImage(postImg.getSource());

        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mShapeBSFragment = new ShapeBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);
        mShapeBSFragment.setPropertiesChangeListener(this);

        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);

        // NOTE(lucianocheng): Used to set integration testing parameters to PhotoEditor
        boolean pinchTextScalable = getIntent().getBooleanExtra(PINCH_TEXT_SCALABLE_INTENT_KEY, true);

        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        photoEditor = new PhotoEditor.Builder(this, postImg)
                .setPinchTextScalable(pinchTextScalable)
                // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        photoEditor.setOnPhotoEditorListener(this);

        //Set Image Dynamically Replace with dabz original
        postImg.getSource().setImageResource(R.drawable.paris_tower);
        postImg.getSource().setScaleType(ImageView.ScaleType.FIT_XY);

        mSaveFileHelper = new FileSaveHelper(this);
    }

    private void handleIntentImage(ImageView source) {
        Intent intent = getIntent();
        if (intent != null) {
            // NOTE(lucianocheng): Using "yoda conditions" here to guard against
            //                     a null Action in the Intent.
            if (Intent.ACTION_EDIT.equals(intent.getAction()) ||
                    ACTION_NEXTGEN_EDIT.equals(intent.getAction())) {
                try {
                    Uri uri = intent.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    source.setImageBitmap(bitmap);
                    source.setScaleType(ImageView.ScaleType.FIT_XY);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                String intentType = intent.getType();
                if (intentType != null && intentType.startsWith("image/")) {
                    Uri imageUri = intent.getData();
                    if (imageUri != null) {
                        source.setImageURI(imageUri);
                        source.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                }
            }
        }
    }

    void initViews() {
        mRootView = findViewById(R.id.root_view_ip);

        closeImg = findViewById(R.id.close_ip_img);
        closeImg.setOnClickListener(this::closeAct);

        imgGallery = findViewById(R.id.gallery_ip_img);
        imgGallery.setOnClickListener(this::selectImage);

        mTxtCurrentTool = findViewById(R.id.current_tool_ip_txt);

        postImg = findViewById(R.id.post_ip_img);

        postTxt = findViewById(R.id.post_ip_txt);
        postTxt.setOnClickListener(this::itemClick);

        captionContainer = findViewById(R.id.caption_ip_container);

        captionEdt = findViewById(R.id.caption_ip_edt);

        imgUndo = findViewById(R.id.undo_ip_img);
        imgUndo.setOnClickListener(this::itemClick);

        imgRedo = findViewById(R.id.redo_ip_img);
        imgRedo.setOnClickListener(this::itemClick);

        mRvTools = findViewById(R.id.tools_constraint_ip_recycler);
        mRvFilters = findViewById(R.id.filter_view_ip_recycler);

    }

    @SuppressLint("NonConstantResourceId")
    public void itemClick(View view) {
        switch (view.getId()) {

            case R.id.undo_ip_img:
                photoEditor.undo();
                break;

            case R.id.redo_ip_img:
                photoEditor.redo();
                break;

            case R.id.post_ip_txt:
                saveImage();
                break;
        }
    }

    private void saveImage() {
        final String fileName = System.currentTimeMillis() + ".png";
        final boolean hasStoragePermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        if (hasStoragePermission || isSdkHigherThan28()) {
            showLoading("Preparing Post...");
            mSaveFileHelper.createFile(fileName, (fileCreated, filePath, error, uri) -> {
                if (fileCreated) {
                    SaveSettings saveSettings = new SaveSettings.Builder()
                            .setClearViewsEnabled(true)
                            .setTransparencyEnabled(true)
                            .build();

                    photoEditor.saveAsFile(filePath, saveSettings, new PhotoEditor.OnSaveListener() {
                        @Override
                        public void onSuccess(@NonNull String imagePath) {
                            mSaveFileHelper.notifyThatFileIsNowPubliclyAvailable(getContentResolver());
                            hideLoading();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    do{
                                        imageUri = uri;
                                    } while (imageUri == null);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            postImg.getSource().setImageURI(imageUri);
                                            postImg.getSource().setScaleType(ImageView.ScaleType.FIT_XY);
                                            createPost();
                                        }
                                    });
                                }
                            }).start();
                        }

                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            hideLoading();
                            showSnackBar("Failed to save Image");
                        }
                    });

                } else {
                    hideLoading();
                    showSnackBar(error);
                }
            });
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void createPost() {
        if(imageUri != null) {
            if(type.equals(NavigationTypes.$POST)) {
                String postId = UUID.randomUUID().toString();
                String fileName = UUID.randomUUID().toString();
                PostLocal post = new PostLocal();
                post.setId(1);
                post.setPostId(postId);
                post.setAuthorId(firebaseUser.getUid());
                if(captionEdt.getText().toString().isEmpty())
                    post.setCaption("None");
                else post.setCaption(captionEdt.getText().toString());
                post.setFileName(fileName);
                post.setUrl(imageUri.toString());
                post.setType(Common.IMAGE);
                post.setDateTime(String.valueOf(System.currentTimeMillis()));

                insertPendingPostLocal(post);
            }
        }
    }

    private void insertPendingPostLocal(PostLocal postLocal) {
        imagePostViewModel.insertPendingPostResult(postLocal).observe(this, aBoolean -> {
            if(aBoolean) {
                showSnackBar("Uploading in background");
            }
        });
    }

    private void setImagePostType() {
        String data = getIntent().getStringExtra(NavigationTypes.$POST_TYPE);
        if(data != null)
            type = data;
        else type = NavigationTypes.$POST;
    }

    private void selectImage(View view) {
        CropImage.activity().setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(1, 1)
                .setFixAspectRatio(false)
                .start(ImagePostAct.this);
    }

    private Uri buildFileProviderUri(@NonNull Uri uri) {
        return FileProvider.getUriForFile(this,
                FILE_PROVIDER_AUTHORITY,
                new File(uri.getPath()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            //imageUri = result.getUri();
            try {
                photoEditor.clearAllViews();
                Uri uri = result.getUri();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                postImg.getSource().setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else Toast.makeText(this, "Oops Something's Wrong", Toast.LENGTH_LONG).show();
    }

    private void closeAct(View view) {
        finish();
    }

/*    @Override
    public void onClick(View v) {

    }*/

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        photoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case SHAPE:
                photoEditor.setBrushDrawingMode(true);
                mShapeBuilder = new ShapeBuilder();
                photoEditor.setShape(mShapeBuilder);
                mTxtCurrentTool.setText(R.string.label_shape);
                showBottomSheetDialogFragment(mShapeBSFragment);
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode) -> {
                    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                    styleBuilder.withTextColor(colorCode);

                    photoEditor.addText(inputText, styleBuilder);
                    mTxtCurrentTool.setText(R.string.label_text);
                });
                break;
            case ERASER:
                photoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser_mode);
                break;
            case FILTER:
                mTxtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                break;
            case EMOJI:
                showBottomSheetDialogFragment(mEmojiBSFragment);
                break;
            case STICKER:
                showBottomSheetDialogFragment(mStickerBSFragment);
                break;
        }
    }

    private void showBottomSheetDialogFragment(BottomSheetDialogFragment fragment) {
        if (fragment == null || fragment.isAdded()) {
            return;
        }
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        photoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void onColorChanged(int colorCode) {
        photoEditor.setShape(mShapeBuilder.withShapeColor(colorCode));
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        photoEditor.setShape(mShapeBuilder.withShapeOpacity(opacity));
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onShapeSizeChanged(int shapeSize) {
        photoEditor.setShape(mShapeBuilder.withShapeSize(shapeSize));
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onShapePicked(ShapeType shapeType) {
        photoEditor.setShape(mShapeBuilder.withShapeType(shapeType));
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        photoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton("Save", (dialog, which) -> saveImage());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("Discard", (dialog, which) -> finish());
        builder.create().show();

    }

    public static final int READ_WRITE_STORAGE = 52;
    private ProgressDialog mProgressDialog;


    public boolean requestPermission(String permission) {
        boolean isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    READ_WRITE_STORAGE);
        }
        return isGranted;
    }

    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    public void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_WRITE_STORAGE:
                isPermissionGranted(grantResults[0] == PERMISSION_GRANTED, permissions[0]);
                break;
        }
    }

    protected void showLoading(@NonNull String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    protected void showSnackBar(@NonNull String message) {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
            //finish();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEditTextChangeListener(View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener((inputText, newColorCode) -> {
            final TextStyleBuilder styleBuilder = new TextStyleBuilder();
            styleBuilder.withTextColor(newColorCode);

            photoEditor.editText(rootView, inputText, styleBuilder);
            mTxtCurrentTool.setText(R.string.label_text);
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onTouchSourceImage(MotionEvent event) {
        Log.d(TAG, "onTouchView() called with: event = [" + event + "]");
    }

}