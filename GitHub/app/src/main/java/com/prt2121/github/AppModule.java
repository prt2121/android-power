package com.prt2121.github;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created by pt2121 on 9/23/15.
 */
@Module public class AppModule {
  private final GitHubApp application;

  public AppModule(GitHubApp application) {
    this.application = application;
  }

  @Provides @Singleton Context provideAppContext() {
    return this.application;
  }
}
