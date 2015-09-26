package com.prt2121.githubsdk.service.user;

import android.content.Context;
import com.prt2121.githubsdk.client.BaseClient;
import com.prt2121.githubsdk.model.response.User;
import rx.Observable;

/**
 * Created by pt2121 on 9/24/15.
 */
public class AuthUser extends BaseClient<User> {

  public AuthUser(Context context, String token) {
    super(context, token);
  }

  @Override public Observable<User> execute() {
    UserService users = getRetrofit().create(UserService.class);
    return users.me();
  }
}
