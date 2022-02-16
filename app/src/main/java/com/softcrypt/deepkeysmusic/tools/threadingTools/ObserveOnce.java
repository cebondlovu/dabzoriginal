package com.softcrypt.deepkeysmusic.tools.threadingTools;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.softcrypt.deepkeysmusic.ui.MainActivity;

public class ObserveOnce {
    final LifecycleOwner owner;

    public ObserveOnce(LifecycleOwner owner) {
        this.owner = owner;
    }

    public <T> void observeOnce(final LiveData<T> liveData, final Observer<T> observer) {
        liveData.observe(owner ,new Observer<T>() {
            @Override
            public void onChanged(T t) {
                liveData.removeObserver(this);
                observer.onChanged(t);
            }
        });
    }
}
