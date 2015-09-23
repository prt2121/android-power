package com.prt2121.githubsdk.model.request;

import com.prt2121.githubsdk.model.response.IssueState;

/**
 * Created by Bernat on 23/08/2014.
 */
public class IssueRequest extends EditIssueRequestDTO {
  public String title;
  public String body;
  public String assignee;
  public Integer milestone;
  public CharSequence[] labels;
  public IssueState state;
}
