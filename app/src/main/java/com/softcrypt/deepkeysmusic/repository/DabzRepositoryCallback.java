package com.softcrypt.deepkeysmusic.repository;

import retrofit2.adapter.rxjava2.Result;

public interface DabzRepositoryCallback<Boolean>{
    void onComplete(Boolean result);
}
