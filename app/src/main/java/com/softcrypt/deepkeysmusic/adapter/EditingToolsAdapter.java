package com.softcrypt.deepkeysmusic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.adapter.listeners.OnItemSelected;
import com.softcrypt.deepkeysmusic.adapter.tools.ToolType;
import com.softcrypt.deepkeysmusic.model.ToolModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EditingToolsAdapter extends RecyclerView.Adapter<EditingToolsAdapter.ViewHolder> {

    private final List<ToolModel> mToolList = new ArrayList<>();
    private final OnItemSelected mOnItemSelected;

    public EditingToolsAdapter(OnItemSelected onItemSelected) {
        mOnItemSelected = onItemSelected;
        mToolList.add(new ToolModel("Shape", R.drawable.ic_oval, ToolType.SHAPE));
        mToolList.add(new ToolModel("Text", R.drawable.ic_text, ToolType.TEXT));
        mToolList.add(new ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER));
        mToolList.add(new ToolModel("Filter", R.drawable.ic_photo_filter, ToolType.FILTER));
        mToolList.add(new ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI));
        mToolList.add(new ToolModel("Sticker", R.drawable.ic_sticker, ToolType.STICKER));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_editing_tools, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        holder.txtTool.setText(item.mToolName);
        holder.imgToolIcon.setImageResource(item.mToolIcon);
    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgToolIcon;
        TextView txtTool;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgToolIcon = itemView.findViewById(R.id.imgToolIcon);
            txtTool = itemView.findViewById(R.id.txtTool);
            itemView.setOnClickListener(v -> mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).mToolType));
        }
    }
}
