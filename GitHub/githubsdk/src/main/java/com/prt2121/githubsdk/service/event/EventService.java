package com.prt2121.githubsdk.service.event;

import com.prt2121.githubsdk.model.response.GitHubEvent;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by pt2121 on 9/26/15.
 */
public interface EventService {

  @GET("/users/{username}/received_events") Observable<List<GitHubEvent>> receivedEvents(
      @Path("username") String username);

  @GET("/users/{username}/received_events") Observable<List<GitHubEvent>> receivedEvents(
      @Path("username") String username, @Query("page") int page);

  @GET("/users/{username}/events") Observable<List<GitHubEvent>> events(
      @Path("username") String username);

  @GET("/users/{username}/events") Observable<List<GitHubEvent>> events(
      @Path("username") String username, @Query("page") int page);
}
