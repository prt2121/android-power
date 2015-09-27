package com.prt2121.githubsdk.service.user;

import android.content.Context;
import com.prt2121.githubsdk.client.BaseClient;
import com.prt2121.githubsdk.model.response.User;
import javax.inject.Inject;
import rx.Observable;

/**
 * Created by pt2121 on 9/24/15.
 */
public class AuthUser extends BaseClient {

  Context context;

  @Inject public AuthUser(Context context) {
    this.context = context;
  }

  public void setAndStoreToken(String token) {
    this.token = token;
    storage.storeToken(token);
  }

  public Observable<User> me() {
    UserService users = getRetrofit().create(UserService.class);
    return users.me();
  }
}
