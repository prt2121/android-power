package com.prt2121.switz;

import java.util.ArrayList;
import java.util.List;

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
    public List<LocType> provideLocTypes() {
        // TODO: fetch from shared pref
        List<LocType> types = new ArrayList<>();
        types.add(new LocType("User", true));
        types.add(new LocType("Bin", true));
        types.add(new LocType("Supermarket/Grocery", true));
        types.add(new LocType("Drop-Off", true));
        return types;
    }

}
