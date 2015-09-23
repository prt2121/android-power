package com.prt2121.githubsdk.model.response.events.payload;

import com.prt2121.githubsdk.model.response.Repo;
import com.prt2121.githubsdk.model.response.User;

public class PublicEventPayload extends GithubEventPayload {

  public Repo repository;
  public User sender;
}
