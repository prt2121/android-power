package com.prt2121.switz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by pt2121 on 3/14/15.
 */
@Module
public class TinyDbModule {

    private Context mContext;

    public TinyDbModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        GsonBuilder b = new GsonBuilder();
        return b.create();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return mContext.getSharedPreferences("tinyDb", MODE_PRIVATE);
    }
}
