package com.prt2121.rxagain;

import android.app.Application;
import timber.log.Timber;

/**
 * Created by pt2121 on 10/30/15.
 */
public class RxApp extends Application {
  @Override public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
  }
}
