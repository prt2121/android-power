package com.prt2121.githubsdk.model.response;

/**
 * Created by Bernat on 30/05/2015.
 */
public class CommitComment extends GithubComment {

  public int position;
  public int line;
  public String commit_id;
  public String path;
}
