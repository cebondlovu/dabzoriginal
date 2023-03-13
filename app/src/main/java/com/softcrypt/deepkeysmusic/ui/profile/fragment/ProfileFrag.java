package com.softcrypt.deepkeysmusic.ui.profile.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.model.User;
import com.softcrypt.deepkeysmusic.model.Verified;
import com.softcrypt.deepkeysmusic.tools.threadingTools.ObserveOnce;
import com.softcrypt.deepkeysmusic.ui.profile.SetupProfileAct;
import com.softcrypt.deepkeysmusic.viewModels.ProfileUserViewModel;
import com.softcrypt.deepkeysmusic.viewModels.ProfileViewModel;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import retrofit2.Callback;

public class ProfileFrag extends Fragment {

    private CircleImageView imgProfile;
    private ImageView optionsImg, closeImg, verifiedImg;
    private ImageButton postsBtn, savedBtn;
    private TextView postsCountTxt, postsTxt, followersCountTxt, followersTxt,
            followingCountTxt, followingTxt, fullnameTxt, usernameTxt,
            emailTxt, msisdnTxt, websiteTxt;
    private SocialTextView bioTxt;
    private Button editProfileBtn;
    private FirebaseUser firebaseUser;

    ProfileUserViewModel profileViewModel;
    private ObserveOnce observeOnce;

    private static boolean isVerified;

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileViewModel.dispose();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((BaseApplication) requireActivity().getApplication()).getAppComponent().injectProfileFrag(this);

        profileViewModel = new ProfileUserViewModel((BaseApplication) requireActivity().getApplication());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        observeOnce = new ObserveOnce(requireActivity());

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfile = v.findViewById(R.id.profile_pf_img);

        optionsImg = v.findViewById(R.id.options_pf_img);
        optionsImg.setOnClickListener(this::showOptions);

        postsCountTxt = v.findViewById(R.id.post_count_pf_txt);

        postsTxt = v.findViewById(R.id.post_pf_txt);
        postsTxt.setOnClickListener(this::navigatePosts);

        followersCountTxt = v.findViewById(R.id.followers_count_pf_txt);

        followersTxt = v.findViewById(R.id.followers_pf_txt);
        followersTxt.setOnClickListener(this::navigateFollowers);

        followingCountTxt = v.findViewById(R.id.following_count_pf_txt);

        followingTxt = v.findViewById(R.id.following_pf_txt);
        followingTxt.setOnClickListener(this::navigateFollowing);

        fullnameTxt = v.findViewById(R.id.fullname_pf_txt);
        bioTxt = v.findViewById(R.id.bio_pf_txt);
        websiteTxt = v.findViewById(R.id.website_pf_txt);
        emailTxt = v.findViewById(R.id.email_pf_txt);
        msisdnTxt = v.findViewById(R.id.msisdn_pf_txt);
        usernameTxt = v.findViewById(R.id.username_pf_txt);
        verifiedImg = v.findViewById(R.id.verified_pf_img);

        editProfileBtn = v.findViewById(R.id.edit_pf_btn);
        editProfileBtn.setOnClickListener(this::navigateEditProfile);

        getUserInfo();
        //getUserPosts();
        return v;
    }

    private void getUserPosts() {
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                if(postSnap == null)
                    observeOnce.observeOnce(profileViewModel.getUserPostsResult(firebaseUser.getUid()), new Observer<DataSnapshot>() {
                        @Override
                        public void onChanged(DataSnapshot dataSnapshot) {
                            if(dataSnapshot != null)
                                postSnap = dataSnapshot;
                        }
                    });
                else {
                    requireActivity().runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            postsCountTxt.setText(postSnap.getChildrenCount() + "");
                        }
                    });
                }
            }
        }).start();*/
/*        profileViewModel.getUserPostsResult(firebaseUser.getUid()).observe(requireActivity(), postSnap -> {
            if(postSnap != null) {
                postsCountTxt.setText(postSnap.getChildrenCount() + "");
            } else postsCountTxt.setText("0");
        });*/
    }

    private void navigateFollowing(View view) {

    }

    private void navigateFollowers(View view) {

    }

    private void navigatePosts(View view) {

    }

    private void showOptions(View view) {

    }

    private void navigateEditProfile(View view) {

    }

    private void getUserInfo() {
        profileViewModel.getProfileResult(firebaseUser.getUid()).observe(requireActivity(), profileSnap->{
            if(profileSnap != null) {
                if (profileSnap.exists()) {
                    User user = profileSnap.getValue(User.class);
                    assert user != null;
                    usernameTxt.setText(user.getUsername());
                    fullnameTxt.setText(user.name);
                    msisdnTxt.setText(user.getMsidn());
                    emailTxt.setText(user.getEmail());

                    if (user.getImageUrl() == null)
                        imgProfile.setImageResource(R.drawable.ic_baseline_insert_emoticon_24);
                    else Picasso.get().load(user.imageUrl)
                            .placeholder(R.drawable.ic_baseline_insert_emoticon_24).into(imgProfile);
                    if (user.getBio() == null)
                        bioTxt.setVisibility(View.GONE);
                    else bioTxt.setText(user.getBio());
                    if (user.getWebsite() == null)
                        websiteTxt.setVisibility(View.GONE);
                    else websiteTxt.setText(user.getWebsite());
                    isVerified(verified -> {
                        if (verified)
                            verifiedImg.setVisibility(View.VISIBLE);
                    });

                }
            }
        });
    }

/*    private boolean isVerified() {
        profileViewModel.getProfileVerified(firebaseUser.getUid()).observe(requireActivity(), verifiedSnap -> {
            if(verifiedSnap.exists()) {
                for (DataSnapshot snapshot : verifiedSnap.getChildren()) {
                    Verified verified = snapshot.getValue(Verified.class);
                    assert verified != null;
                    isVerified = verified.isVerified();
                }
            } else isVerified = verifiedSnap.exists();
        });
        return isVerified;
    }*/

    private void isVerified(Consumer<Boolean> callback) {
        AtomicBoolean isVerified = new AtomicBoolean(false); // or null
        profileViewModel.getProfileVerified(firebaseUser.getUid()).observe(requireActivity(), verifiedSnap -> {
            try {
                if (verifiedSnap.exists()) {
                    Verified verified = verifiedSnap.getChildren().iterator().next().getValue(Verified.class);
                    assert verified != null;
                    isVerified.set(verified.isVerified());
                }
                callback.accept(isVerified.get());
            } catch (Exception e) {

            }

        });
    }


}