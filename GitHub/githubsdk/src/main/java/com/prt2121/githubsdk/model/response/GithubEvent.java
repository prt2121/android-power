package com.prt2121.githubsdk.model.response;

import com.google.gson.annotations.SerializedName;
import com.prt2121.githubsdk.model.response.events.EventType;

/**
 * Created by Bernat on 31/08/2014.
 */
public class GithubEvent {
  public long id;
  public EventType type = EventType.Unhandled;
  public String name;
  public User actor;
  public User org;
  public Repo repo;
  public Object payload;

  @SerializedName("public") public boolean public_event;

  public String created_at;

  public EventType getType() {
    return type != null ? type : EventType.Unhandled;
  }
}
