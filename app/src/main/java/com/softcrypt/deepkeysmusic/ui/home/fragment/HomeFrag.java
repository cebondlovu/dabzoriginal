package com.softcrypt.deepkeysmusic.ui.home.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.adapter.PostAdapter;
import com.softcrypt.deepkeysmusic.adapter.StoryAdapter;
import com.softcrypt.deepkeysmusic.base.BaseApplication;
import com.softcrypt.deepkeysmusic.common.DisplayableError;
import com.softcrypt.deepkeysmusic.common.NavigationTypes;
import com.softcrypt.deepkeysmusic.model.Post;
import com.softcrypt.deepkeysmusic.ui.notification.NotificationsAct;
import com.softcrypt.deepkeysmusic.ui.post.ImagePostAct;
import com.softcrypt.deepkeysmusic.viewModels.HomeViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class HomeFrag extends Fragment {

    private static Runnable runnable;
    ImageView notificationImg, logoImg, addStoryImg;
    CircleImageView profileImg;
    RecyclerView recyclerStory, recyclerPosts;
    TextView addStoryTxt;
    SwipeRefreshLayout swipeRefreshLayout;
    HorizontalScrollView storyNested;
    StoryAdapter storyAdapter;
    PostAdapter postAdapter;

    Handler handler;

    private LinearLayoutManager mLayoutManager;

    private int mTotalItemCount = 0;
    private int mFirstVisibleItemPosition;
    private int mVisibleItemCount;
    private boolean mIsLoading = false, mIsLastPage = false;
    private int mPostsPerPage = 10, count = 0, mPageSize = 1;

    private HomeViewModel homeViewModel;
    private DisplayableError displayableError;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ((BaseApplication) requireActivity().getApplication()).getAppComponent().injectHomeFrag(this);

        homeViewModel = new HomeViewModel((BaseApplication) requireActivity().getApplication(), Realm.getDefaultInstance());

        displayableError = DisplayableError.getInstance(requireActivity());

        handler = new Handler();

        logoImg = v.findViewById(R.id.logo_h_img);

        notificationImg = v.findViewById(R.id.notification_h_img);
        notificationImg.setOnClickListener(this::navigateNotifications);

        storyNested = v.findViewById(R.id.story_scroll_h);//262

        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_h);
        swipeRefreshLayout.setColorSchemeResources(R.color.main_clr, R.color.primaryDark,
                R.color.comment, R.color.flames);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getContext()
                .getResources().getColor(R.color.transparentLight));

        addStoryImg = v.findViewById(R.id.add_story_h_img);
        addStoryImg.setOnClickListener(this::addStory);

        addStoryTxt = v.findViewById(R.id.add_story_h_txt);
        addStoryTxt.setOnClickListener(this::addStory);

        profileImg = v.findViewById(R.id.profile_h_img);
        profileImg.setOnClickListener(this::addStory);

        recyclerPosts = v.findViewById(R.id.recycler_view_h_posts);
        recyclerStory = v.findViewById(R.id.recycler_view_h_stories);

        mLayoutManager = new LinearLayoutManager(requireActivity());
        //mLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerPosts.setLayoutManager(mLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerPosts.getContext(),
                mLayoutManager.getOrientation());
        recyclerPosts.addItemDecoration(dividerItemDecoration);

        postAdapter = new PostAdapter(homeViewModel, requireActivity(), getContext());
        recyclerPosts.setAdapter(postAdapter);

        homeViewModel.getPostLiveData().observe(getViewLifecycleOwner(), posts -> {
            Collections.sort(posts, new Comparator<Post>() {
                @Override
                public int compare(Post post, Post t1) {
                    return Long.compare(t1.getDateTime(), post.getDateTime());
                }
            });
            postAdapter.clear();
            postAdapter.addAll(posts);
            mIsLoading = false;
            swipeRefreshLayout.setRefreshing(false);
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPost();
            }

        });

        recyclerPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        handler.postDelayed(getViewRunnable(), 5000);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        handler.removeCallbacks(runnable);
                        storyNested.setVisibility(View.GONE);
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
                        getPost();
                    }
                }
            }
        });

        getPost();

        return v;
    }

    private Runnable getViewRunnable() {
        runnable = new Runnable() {
            @Override
            public void run() {
                storyNested.setVisibility(View.VISIBLE);
                Log.d("FB_ART", "IDLE");//12
            }
        };
        return runnable;
    }

    private void getPost() {
        swipeRefreshLayout.setRefreshing(true);
        mIsLoading = true;
        if(homeViewModel.getPosts(mPostsPerPage, mPageSize)) {
            swipeRefreshLayout.setRefreshing(false);
            mIsLoading = false;
        }
    }

    private void navigateNotifications(View view) {
        startActivity(new Intent(getContext(), NotificationsAct.class));
    }

    private void addStory(View view) {
        Intent intent = new Intent(getContext(), ImagePostAct.class);
        intent.putExtra(NavigationTypes.$POST_TYPE, NavigationTypes.$STORY);
        startActivity(intent);
    }

}