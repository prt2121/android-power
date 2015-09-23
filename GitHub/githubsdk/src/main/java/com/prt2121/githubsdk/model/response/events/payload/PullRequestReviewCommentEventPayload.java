package com.prt2121.githubsdk.model.response.events.payload;

import com.prt2121.githubsdk.PullRequest;
import com.prt2121.githubsdk.model.response.CommitComment;

public class PullRequestReviewCommentEventPayload extends ActionEventPayload {
  public PullRequest pull_request;
  public CommitComment comment;
}
