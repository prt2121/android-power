package com.prt2121.githubsdk.model.response.events.payload;

import com.prt2121.githubsdk.model.response.UserType;

/**
 * Created by Bernat on 05/10/2014.
 */
public class DeleteEventPayload extends GithubEventPayload {
  public String ref;
  public String ref_type;
  public UserType pusher_type;
}
