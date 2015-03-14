package com.prt2121.switz;

import com.google.gson.Gson;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by pt2121 on 3/13/15.
 *
 * LocTypeModule provides user's selected location types.
 */
@Module
public class LocTypeModule {

    @Provides
    @Singleton
    public LocType[] provideLocTypes(SharedPreferences preferences, Gson gson) {
        String s = preferences.getString("locType", null);
        LocType[] types = gson.fromJson(s, LocType[].class);
        if (types == null) {
            types = new LocType[4];
            types[0] = new LocType("User", true);
            types[1] = new LocType("Bin", true);
            types[2] = new LocType("Supermarket/Grocery", true);
            types[3] = new LocType("Drop-Off", true);
            SharedPreferences.Editor e = preferences.edit();
            e.putString("locType", gson.toJson(types));
            e.apply();
        }
        return types;
    }

}
