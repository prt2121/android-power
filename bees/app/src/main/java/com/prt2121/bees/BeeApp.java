package com.prt2121.bees;

import com.prt2121.bees.userlocation.UserLocationModule;

import android.app.Application;

/**
 * Created by pt2121 on 9/9/15.
 */
public class BeeApp extends Application {

    private static BeeApp mInstance;

    private Graph mGraph;

    public static BeeApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initDaggerGraph();
    }

    private void initDaggerGraph() {
        mGraph = DaggerGraph.builder()
                .userLocationModule(new UserLocationModule(getApplicationContext()))
                .build();
    }

    public Graph getGraph() {
        return mGraph;
    }
}
