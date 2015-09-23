package com.prt2121.githubsdk.model.response.events.payload;

import com.google.gson.annotations.SerializedName;
import com.prt2121.githubsdk.PullRequest;
import com.prt2121.githubsdk.model.response.Organization;

/**
 * Created by Bernat on 05/10/2014.
 */
public class PullRequestEventPayload extends ActionEventPayload {
  public int number;
  public PullRequest pull_request;

  @SerializedName("public") public boolean is_public;
  public Organization org;
  public String created_at;
}
