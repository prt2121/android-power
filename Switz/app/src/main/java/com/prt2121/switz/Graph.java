package com.prt2121.switz;

import com.google.gson.Gson;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by pt2121 on 3/13/15.
 */
@Singleton
@Component(modules = {
        LocTypeModule.class,
        TinyDbModule.class
})
public interface Graph {

    LocType[] locTypes();

    Gson gson();

    SharedPreferences sharedPreferences();

    void inject(LocTypeFragment fragment);

    void inject(LocTypeAdapter adapter);

}
