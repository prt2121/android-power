package com.prt2121.githubsdk.service.user;

import com.prt2121.githubsdk.model.response.User;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by pt2121 on 9/24/15.
 */
public interface UserService {
  @GET("/user") Observable<User> me();
}
