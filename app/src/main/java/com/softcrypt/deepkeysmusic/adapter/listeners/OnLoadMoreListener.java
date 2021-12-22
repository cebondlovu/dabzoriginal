package com.softcrypt.deepkeysmusic.adapter.listeners;

public interface OnLoadMoreListener {
    void onCurrentItemId(String postId);

    void onLastItem(String postId);

    void onFinish();
}
