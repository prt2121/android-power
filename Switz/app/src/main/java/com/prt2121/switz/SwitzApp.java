package com.prt2121.switz;

import android.app.Application;

/**
 * Created by pt2121 on 3/13/15.
 */
public class SwitzApp extends Application {

    private static SwitzApp mInstace;

    private Graph mGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstace = this;
        mGraph = Dagger_Graph.builder()
                .locTypeModule(new LocTypeModule())
                .build();
    }

    public static SwitzApp getInstance() {
        return mInstace;
    }

    public Graph getGraph() {
        return mGraph;
    }

}
