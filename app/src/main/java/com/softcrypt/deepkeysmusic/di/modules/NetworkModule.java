package com.softcrypt.deepkeysmusic.di.modules;

import com.softcrypt.deepkeysmusic.remote.IDabzApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public abstract class NetworkModule {

    private static String $BASE_URL = "https://deep-keys-music-default-rtdb.europe-west1.firebasedatabase.app/";

    @Provides
    @Singleton
    static Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl($BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    static IDabzApi provideIDabzApi(Retrofit retrofit) {
        return  retrofit.create(IDabzApi.class);
    }
}
