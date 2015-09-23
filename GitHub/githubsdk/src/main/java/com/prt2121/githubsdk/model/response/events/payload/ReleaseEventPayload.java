package com.prt2121.githubsdk.model.response.events.payload;

import com.prt2121.githubsdk.model.response.Release;
import com.prt2121.githubsdk.model.response.Repo;
import com.prt2121.githubsdk.model.response.User;

/**
 * Created by Bernat on 30/05/2015.
 */
public class ReleaseEventPayload extends GithubEventPayload {

  public String action;
  public Release release;
  public Repo repository;
  public User sender;
}
