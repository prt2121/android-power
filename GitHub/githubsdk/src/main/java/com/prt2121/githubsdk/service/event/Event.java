package com.prt2121.githubsdk.service.event;

import android.content.Context;
import com.prt2121.githubsdk.client.BaseClient;
import com.prt2121.githubsdk.model.response.GitHubEvent;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;

/**
 * Created by pt2121 on 9/26/15.
 */
public class Event extends BaseClient {

  private String username;

  @Inject public Event(Context context) {
    super(context);
  }

  public Event forUser(String username) {
    this.username = username;
    return this;
  }

  public Observable<List<GitHubEvent>> receivedEvents() {
    EventService service = getRetrofit().create(EventService.class);
    return service.receivedEvents(username);
  }

  public Observable<List<GitHubEvent>> events() {
    EventService service = getRetrofit().create(EventService.class);
    return service.events(username);
  }

  public Observable<List<GitHubEvent>> events(int page) {
    EventService service = getRetrofit().create(EventService.class);
    return service.events(username, page);
  }
}