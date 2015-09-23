package com.prt2121.githubsdk.model.response.events.payload;

import com.prt2121.githubsdk.model.response.GithubComment;
import com.prt2121.githubsdk.model.response.Issue;

/**
 * Created by Bernat on 05/10/2014.
 */
public class IssueCommentEventPayload extends ActionEventPayload {
  public Issue issue;
  public GithubComment comment;
}
