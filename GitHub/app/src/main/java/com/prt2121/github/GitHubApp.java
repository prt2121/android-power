package com.prt2121.github;

import android.app.Application;
import timber.log.Timber;

/**
 * Created by pt2121 on 9/23/15.
 */
public class GitHubApp extends Application {

  private AppComponent appComponent;

  @Override public void onCreate() {
    super.onCreate();
    this.initializeInjector();
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
    RxActivityLifecycleCallbacks.init(this);
  }

  private void initializeInjector() {
    this.appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
  }

  public AppComponent getAppComponent() {
    return this.appComponent;
  }
}
