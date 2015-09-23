package com.prt2121.githubsdk.model.response.events.payload;

import com.prt2121.githubsdk.model.response.CommitComment;
import com.prt2121.githubsdk.model.response.Repo;
import com.prt2121.githubsdk.model.response.User;

/**
 * Created by Bernat on 05/10/2014.
 */
public class CommitCommentEventPayload extends ActionEventPayload {
  public CommitComment comment;
  public Repo repository;
  public User sender;
}
