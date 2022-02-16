package com.softcrypt.deepkeysmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.common.DisplayableError;
import com.softcrypt.deepkeysmusic.common.NavigationTypes;
import com.softcrypt.deepkeysmusic.model.Comment;
import com.softcrypt.deepkeysmusic.model.Post;
import com.softcrypt.deepkeysmusic.viewModels.CommentViewModel;
import com.softcrypt.deepkeysmusic.viewModels.HomeViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private final List<Comment> commentList;
    private final Context context;
    private final CommentViewModel commentViewModel;
    private LifecycleOwner owner;
    private DisplayableError displayableError;
    private FirebaseUser firebaseUser;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public CommentAdapter(CommentViewModel commentViewModel, LifecycleOwner owner, Context context) {
        this.commentList = new ArrayList<>();
        this.context = context;
        this.commentViewModel = commentViewModel;
        this.owner = owner;
        this.displayableError = DisplayableError.getInstance(context);
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(holder.getBindingAdapterPosition());

        if(comment.commentType.equals(NavigationTypes.$VOICE_NOTE) || comment.commentType.equals(NavigationTypes.$VOICE_NOTE_TEXT)) {
            if(comment.commentType.equals(NavigationTypes.$VOICE_NOTE)) {
                holder.commentTxt.setVisibility(View.GONE);
                holder.mediaImg.setVisibility(View.GONE);
                holder.vnContainer.setVisibility(View.VISIBLE);
            }
            if(comment.commentType.equals(NavigationTypes.$VOICE_NOTE_TEXT)) {
                holder.commentTxt.setVisibility(View.VISIBLE);
                holder.mediaImg.setVisibility(View.GONE);
                holder.vnContainer.setVisibility(View.VISIBLE);
            }
        } else if(comment.commentType.equals(NavigationTypes.$IMAGE) || comment.commentType.equals(NavigationTypes.$IMAGE_TEXT)) {
            if(comment.commentType.equals(NavigationTypes.$IMAGE)) {
                holder.commentTxt.setVisibility(View.GONE);
                holder.mediaImg.setVisibility(View.VISIBLE);
                holder.vnContainer.setVisibility(View.GONE);
            }
            if(comment.commentType.equals(NavigationTypes.$IMAGE_TEXT)) {
                holder.commentTxt.setVisibility(View.VISIBLE);
                holder.mediaImg.setVisibility(View.VISIBLE);
                holder.vnContainer.setVisibility(View.GONE);
            }
        } else {
            holder.commentTxt.setVisibility(View.VISIBLE);
            holder.mediaImg.setVisibility(View.GONE);
            holder.vnContainer.setVisibility(View.GONE);
        }

        if(comment.publisherId.equals(FirebaseAuth.getInstance().getUid())) {
            holder.deleteCommentImg.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addAll(List<Comment> newComments) {
        int initialSize = commentList.size();
        commentList.addAll(newComments);
        notifyItemRangeInserted(initialSize, newComments.size());
    }

    public String getLastItemId() {
        return commentList.get(commentList.size()-1).getCommentId();
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
            if((diff / DAY_MILLIS) > 28 && (diff / DAY_MILLIS) < 60)
                return "a month ago";
            if((diff / DAY_MILLIS) > 60)
                return "a while ago";
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView deleteCommentImg, mediaImg, playImg, replyImg;
        private final CircleImageView profileImg;
        private final SocialTextView commentTxt;
        private final TextView usernameTxt, vnProgressTxt, datetimeTxt, replyTxt;
        private final ProgressBar vnProgress;
        private final LinearLayout vnContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            deleteCommentImg = itemView.findViewById(R.id.delete_ic_img);
            mediaImg = itemView.findViewById(R.id.media_comment_ic_img);
            playImg = itemView.findViewById(R.id.play_ic_img);
            replyImg = itemView.findViewById(R.id.reply_ic_img);

            profileImg = itemView.findViewById(R.id.profile_ic_img);

            commentTxt = itemView.findViewById(R.id.comment_ic_txt);

            usernameTxt = itemView.findViewById(R.id.username_ic_txt);
            vnProgressTxt = itemView.findViewById(R.id.vn_progress_ic_txt);
            datetimeTxt = itemView.findViewById(R.id.datetime_ic_txt);
            replyTxt = itemView.findViewById(R.id.reply_ic_txt);

            vnProgress = itemView.findViewById(R.id.vn_progress_ic_pb);

            vnContainer = itemView.findViewById(R.id.vn_container_ic);
        }
    }
}
