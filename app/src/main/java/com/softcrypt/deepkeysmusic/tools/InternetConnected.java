package com.softcrypt.deepkeysmusic.tools;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.softcrypt.deepkeysmusic.common.DisplayableError;

public class InternetConnected extends AsyncTaskLoader<Boolean> {

    public InternetConnected(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public Boolean loadInBackground() {
        return isDataConnected();
    }

    @Override
    public void deliverResult(@Nullable Boolean data) {
        if(isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }

    private Boolean isDataConnected() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 8.8.8.8");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }
}
