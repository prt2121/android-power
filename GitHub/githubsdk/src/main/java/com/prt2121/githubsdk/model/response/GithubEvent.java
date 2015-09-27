package com.prt2121.githubsdk.model.response;

import com.google.gson.annotations.SerializedName;
import com.prt2121.githubsdk.model.response.events.EventType;

/**
 * Created by Bernat on 31/08/2014.
 */
public class GitHubEvent {
  public long id;
  public EventType type = EventType.Unhandled;
  public String name;
  public User actor;
  public User org;
  public Repo repo;
  public Object payload;

  @SerializedName("public") public boolean publicEvent;

  public String created_at;

  public EventType getType() {
    return type != null ? type : EventType.Unhandled;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GitHubEvent that = (GitHubEvent) o;

    if (id != that.id) return false;
    if (publicEvent != that.publicEvent) return false;
    if (type != that.type) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (actor != null ? !actor.equals(that.actor) : that.actor != null) return false;
    if (org != null ? !org.equals(that.org) : that.org != null) return false;
    if (repo != null ? !repo.equals(that.repo) : that.repo != null) return false;
    if (payload != null ? !payload.equals(that.payload) : that.payload != null) return false;
    return !(created_at != null ? !created_at.equals(that.created_at) : that.created_at != null);
  }

  @Override public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (actor != null ? actor.hashCode() : 0);
    result = 31 * result + (org != null ? org.hashCode() : 0);
    result = 31 * result + (repo != null ? repo.hashCode() : 0);
    result = 31 * result + (payload != null ? payload.hashCode() : 0);
    result = 31 * result + (publicEvent ? 1 : 0);
    result = 31 * result + (created_at != null ? created_at.hashCode() : 0);
    return result;
  }

  @Override public String toString() {
    return "GitHubEvent{" +
        "id=" + id +
        ", type=" + type +
        ", name='" + name + '\'' +
        ", actor=" + actor +
        ", org=" + org +
        ", repo=" + repo +
        ", payload=" + payload +
        ", publicEvent=" + publicEvent +
        ", created_at='" + created_at + '\'' +
        '}';
  }
}
