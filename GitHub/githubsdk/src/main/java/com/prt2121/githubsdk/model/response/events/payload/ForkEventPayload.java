package com.prt2121.githubsdk.model.response.events.payload;

import com.prt2121.githubsdk.model.response.Repo;

/**
 * Created by Bernat on 05/10/2014.
 */
public class ForkEventPayload extends GithubEventPayload {
  public Repo forkee;
  public Repo repository;
}
