package com.softcrypt.deepkeysmusic.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.adapter.listeners.OnLoadMoreListener;
import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.common.DataPaths;
import com.softcrypt.deepkeysmusic.common.DisplayableError;
import com.softcrypt.deepkeysmusic.common.ParseData;
import com.softcrypt.deepkeysmusic.model.MediaUrl;
import com.softcrypt.deepkeysmusic.model.Post;
import com.softcrypt.deepkeysmusic.model.User;
import com.softcrypt.deepkeysmusic.ui.comment.CommentAct;
import com.softcrypt.deepkeysmusic.viewModels.HomeViewModel;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final List<Post> postList;
    private final Context context;
    private final HomeViewModel homeViewModel;
    private LifecycleOwner owner;
    private DisplayableError displayableError;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private FirebaseUser firebaseUser;

    public PostAdapter(HomeViewModel homeViewModel, LifecycleOwner owner, Context context) {
        this.homeViewModel = homeViewModel;
        this.owner = owner;
        this.context = context;
        this.displayableError = DisplayableError.getInstance(context);
        this.postList = new ArrayList<>();
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Post post = postList.get(holder.getBindingAdapterPosition());
        getProfileInfo(holder.name, holder.username, holder.profileImg, holder.verified,post);
        isLiked(post, holder.flameImg, holder.flameCount);
        if(post.getMediaType().equals(Common.IMAGE))
            Picasso.get().load(post.getMedia()).into(holder.postMedia);
        holder.dateTime.setText(getTimeAgo(post.getDateTime()));
        theresComments(post, holder.commentCount, holder.commentImg);
        holder.caption.setText(post.getCaption());
        holder.flameImg.setTag("like");

        holder.flameImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.flameImg.getTag().equals("like")) {
                    holder.flameImg.setImageResource(R.drawable.ic_twotone_local_fire_department_24);
                    holder.flameImg.setTag("liked");
                    likePost(post, firebaseUser.getUid());
                    sendNotification(post.getPostId(), post.getAuthorId());
                } else {
                    holder.flameImg.setImageResource(R.drawable.ic_outline_local_fire_department_24);
                    holder.flameImg.setTag("like");
                    unlikePost(post, firebaseUser.getUid());
                }
            }
        });

        holder.commentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentAct.class);
                intent.putExtra(ParseData.$POST_ID, post.getPostId());
                intent.putExtra(ParseData.$AUTHOR_ID, post.getAuthorId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView profileImg;
        private final ImageView postMedia, options, flameImg, commentImg, save, verified;
        private final SocialTextView caption;
        private final TextView username, name, flameCount, commentCount, dateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_pr_img);
            postMedia = itemView.findViewById(R.id.media_pr_img);
            options = itemView.findViewById(R.id.more_pr_img);
            flameImg = itemView.findViewById(R.id.flame_pr_img);
            commentImg = itemView.findViewById(R.id.comment_pr_img);
            save = itemView.findViewById(R.id.save_pr_img);
            verified = itemView.findViewById(R.id.verified_pr_img);

            caption = itemView.findViewById(R.id.caption_pr_txt);
            username = itemView.findViewById(R.id.username_pr_txt);
            name = itemView.findViewById(R.id.author_pr_txt);
            dateTime = itemView.findViewById(R.id.datetime_pr_txt);
            flameCount = itemView.findViewById(R.id.flame_pr_txt);
            commentCount = itemView.findViewById(R.id.comment_pr_txt);
        }
    }

    public void addAll(List<Post> newPosts) {
        int initialSize = postList.size();
        postList.addAll(newPosts);
        notifyItemRangeInserted(initialSize, newPosts.size());
    }

    public String getLastItemId() {
        return postList.get(postList.size()-1).getPostId();
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }


        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public String prettyCount(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    private void sendNotification(String postId, String userId) {
        if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userId)) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            map.put("text", Common.$LIKED);
            map.put("postId", postId);
            map.put("isPost", true);

            homeViewModel.sendNotification(userId, map);
        }
    }

    private void likePost(Post post, String uid) {
        homeViewModel.likePost(post.getPostId(), uid);
    }

    private void unlikePost(Post post, String uid) {
        homeViewModel.unlike(post.getPostId(), uid);
    }

    @SuppressLint("SetTextI18n")
    private void isLiked(Post post, ImageView imageView, TextView text) {
        homeViewModel.isLikedResult(post.getPostId()).observe(owner, isLikedSnap -> {
            if(Objects.equals(isLikedSnap.getKey(), post.getPostId())) {
                if (isLikedSnap.getChildrenCount() == 1)
                    text.setText(prettyCount(isLikedSnap.getChildrenCount()) + " Flame");
                else if (isLikedSnap.getChildrenCount() > 1)
                    text.setText(prettyCount(isLikedSnap.getChildrenCount()) + " Flames");
                else {
                    text.setText("");
                }
            }

            for (DataSnapshot snapshot : isLikedSnap.getChildren()) {
                if (Objects.equals(isLikedSnap.getKey(), post.getPostId())) {
                    if (Objects.equals(snapshot.getKey(), firebaseUser.getUid())) {
                        imageView.setImageResource(R.drawable.ic_twotone_local_fire_department_24);
                        imageView.setTag("liked");
                    }
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void theresComments(Post post, TextView commentCount, ImageView imageView) {
        homeViewModel.theresCommentsResult(post.getPostId()).observe(owner, commentsSnap -> {
            if(Objects.equals(commentsSnap.getKey(), post.getPostId())) {
                if(commentsSnap.getChildrenCount() < 1) {
                    commentCount.setText("");
                } else {
                    if(commentsSnap.getChildrenCount() == 1) {
                        commentCount.setText(prettyCount(commentsSnap.getChildrenCount()) + "Comment");
                    } else {
                        commentCount.setText(prettyCount(commentsSnap.getChildrenCount()) + "Comments");
                    }
                }
            }

            for (DataSnapshot snapshot : commentsSnap.getChildren()) {
                if (Objects.equals(commentsSnap.getKey(), post.getPostId())) {
                    if (Objects.equals(snapshot.getKey(), firebaseUser.getUid())) {
                        imageView.setImageResource(R.drawable.ic_baseline_chat_bubble_24);
                    }
                }
            }
        });
    }

    private void getProfileInfo(TextView name, TextView username, CircleImageView profileImg, ImageView verified,Post post) {
        homeViewModel.getProfileResult(post.getAuthorId()).observe(owner, profileSnap -> {
            User user = profileSnap.getValue(User.class);
            assert user != null;
            if(user.getId().equals(post.getAuthorId())) {
                name.setText(Objects.requireNonNull(user.getName()));
                username.setText(Objects.requireNonNull(user.getUsername()));
                Picasso.get().load(Objects.requireNonNull(user.getImageUrl())).into(profileImg);
            }
        });
    }
}
