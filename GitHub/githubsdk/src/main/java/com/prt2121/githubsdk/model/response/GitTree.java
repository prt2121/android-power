package com.prt2121.githubsdk.model.response;

import java.util.List;

public class GitTree extends ShaUrl {

  public List<GitTreeEntry> tree;
  public boolean truncated;
}
