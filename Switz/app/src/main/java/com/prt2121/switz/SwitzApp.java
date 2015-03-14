package com.prt2121.switz;

import android.app.Application;

/**
 * Created by pt2121 on 3/13/15.
 */
public class SwitzApp extends Application {

    private static SwitzApp mInstance;

    private Graph mGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mGraph = Dagger_Graph.builder()
                .tinyDbModule(new TinyDbModule(getApplicationContext()))
                .locTypeModule(new LocTypeModule())
                .build();
    }

    public static SwitzApp getInstance() {
        return mInstance;
    }

    public Graph getGraph() {
        return mGraph;
    }

}
