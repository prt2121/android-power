package com.prt2121.githubsdk.model.response.events.payload;

import com.prt2121.githubsdk.model.response.Repo;
import com.prt2121.githubsdk.model.response.Team;

public class TeamAddEventPayload extends GithubEventPayload {
  public Team team;
  public Repo repository;
}
