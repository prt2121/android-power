package com.prt2121.github;

import com.prt2121.github.auth.WebLoginActivity;
import dagger.Component;
import javax.inject.Singleton;

/**
 * Created by pt2121 on 9/23/15.
 */
@Singleton @Component(modules = AppModule.class) public interface AppComponent {
  void inject(MainActivity activity);

  void inject(WebLoginActivity activity);
}
