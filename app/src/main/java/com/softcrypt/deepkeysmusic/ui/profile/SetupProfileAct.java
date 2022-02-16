package com.softcrypt.deepkeysmusic.ui.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;
import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.common.DataPaths;
import com.softcrypt.deepkeysmusic.common.DisplayableError;
import com.softcrypt.deepkeysmusic.common.ErrorFactory;
import com.softcrypt.deepkeysmusic.model.User;
import com.softcrypt.deepkeysmusic.ui.MainActivity;
import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.viewModels.ProfileViewModel;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupProfileAct extends AppCompatActivity {

    private CountryCodePicker countryCodePicker;
    private AppCompatEditText usernameEdt, nameEdt, emailEdt, msisdnEdt;
    private CircleImageView profileImg;
    private AppCompatTextView addImageTxt;
    private AppCompatButton saveBtn;
    private Uri imageUri;
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressBar progressBar;
    private static boolean updated = false;
    private static String email;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    ProfileViewModel profileViewModel;

    DisplayableError displayableError;

    @Override
    protected void onStart() {
        super.onStart();
        profileViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(ProfileViewModel.class);
        displayableError = DisplayableError.getInstance(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        ((BaseApplication)getApplication()).getAppComponent().injectSetupProfileAct(this);
        countryCodePicker = findViewById(R.id.code_picker);

        progressBar = findViewById(R.id.loading_sp_pb);

        profileImg = findViewById(R.id.sp_profile_img);
        profileImg.setOnClickListener(this::addProfilePicture);

        addImageTxt = findViewById(R.id.add_image_txt);
        addImageTxt.setOnClickListener(this::addProfilePicture);

        usernameEdt = findViewById(R.id.username_sp_edt);
        nameEdt = findViewById(R.id.name_sp_edt);
        emailEdt = findViewById(R.id.email_sp_edt);
        msisdnEdt = findViewById(R.id.msisdn_sp_edt);

        saveBtn = findViewById(R.id.setup_sp_btn);
        saveBtn.setOnClickListener(this::setupProfile);

        getEmailData();
    }

    private void getEmailData() {
        Intent intent = getIntent();
        emailEdt.setText(intent.getStringExtra(Common.EMAIL));
        emailEdt.setEnabled(false);
    }

    public void addProfilePicture(View view) {
        CropImage.activity().setCropShape(CropImageView.CropShape.OVAL)
                .start(SetupProfileAct.this);
    }

    public void setupProfile(View view) {
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.GONE);
        if(Objects.requireNonNull(usernameEdt.getText()).toString().isEmpty() ||
                Objects.requireNonNull(nameEdt.getText()).toString().isEmpty() ||
                Objects.requireNonNull(msisdnEdt.getText()).toString().isEmpty()) {

            progressBar.setVisibility(View.GONE);
            saveBtn.setVisibility(View.VISIBLE);

            if(Objects.requireNonNull(usernameEdt.getText()).toString().isEmpty())
                usernameEdt.setError(ErrorFactory.throwableMessage(1, Common.USERNAME
                ));
            if(Objects.requireNonNull(nameEdt.getText()).toString().isEmpty())
                nameEdt.setError(ErrorFactory.throwableMessage(1, Common.NAME
                ));
            if(Objects.requireNonNull(msisdnEdt.getText()).toString().isEmpty())
                msisdnEdt.setError(ErrorFactory.throwableMessage(1, Common.PHONE_NUMBER
                ));

        } else {
           updateImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                assert result != null;
                imageUri = result.getUri();
                updateImage();
            } else {
                displayableError.createToastMessage(ErrorFactory.throwableMessage(0,""),0);
            }
        } else displayableError.createToastMessage(ErrorFactory.throwableMessage(6,""),2);
    }

    private void updateImage() {
        if(imageUri != null) {
            Picasso.get().load(imageUri).into(profileImg);
            String fileName = UUID.randomUUID().toString();
            String reference = DataPaths.$IMAGE_PATH + fileName;

            profileViewModel.getUploadResult(reference, imageUri).observe(
                    this, this::createProfile);

        } else createProfile(null);
    }

    private void createProfile(String results) {
        if(firebaseUser == null) {
            displayableError.createToastMessage("Cannot Connect To Server", 0);
            progressBar.setVisibility(View.GONE);
            saveBtn.setVisibility(View.VISIBLE);
        } else {
            countryCodePicker.registerCarrierNumberEditText(msisdnEdt);
            User user = new User();
            user.setId(firebaseUser.getUid());
            user.setUsername(Objects.requireNonNull(usernameEdt.getText()).toString());
            user.setName(Objects.requireNonNull(nameEdt.getText()).toString());
            user.setEmail(Objects.requireNonNull(emailEdt.getText()).toString());
            user.setMsidn(Objects.requireNonNull(msisdnEdt.getText()).toString());
            user.setBio(null);
            user.setLocation(countryCodePicker.getSelectedCountryName());
            user.setImageUrl(results);
            user.setWebsite(null);
            user.setTimeStamp(System.currentTimeMillis());

            profileViewModel.getCreateProfileResult(firebaseUser.getUid(), user).observe(
                    this, aBoolean -> {
                        if (aBoolean) {
                            if (profileVerified()) {
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            }
                        } else showOperationProfileCreationError();
                    });
        }
    }

    private boolean profileVerified() {
        String mail = Objects.requireNonNull(emailEdt.getText()).toString()
                .replaceAll("[.@]*", "");
        HashMap<String , Object> map = new HashMap<>();
        map.put("verified", true);
        profileViewModel.getUpdateProfileVerifiedResult(mail, map).observe(this, aBoolean -> {
            updated = aBoolean;
        });
        return updated;
    }

    private void showOperationProfileCreationError() {

    }
}