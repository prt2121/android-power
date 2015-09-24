package com.prt2121.githubsdk.service.repos;

import com.prt2121.githubsdk.model.response.Repo;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by pt2121 on 9/22/15.
 */
public interface ReposService {
  @GET("/users/{user}/repos") Observable<List<Repo>> repos(@Path("user") String user,
      @Query("sort") String sort);

  @GET("/user/repos?type=owner") Observable<List<Repo>> repos(@Query("sort") String sort);
}
