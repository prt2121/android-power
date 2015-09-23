package com.prt2121.githubsdk.model.request;

import com.prt2121.githubsdk.model.response.IssueState;

/**
 * Created by Bernat on 15/04/2015.
 */
public class CreateMilestoneRequestDTO {

  public String title;
  public String description;
  public String due_on;
  public IssueState state;

  public CreateMilestoneRequestDTO(String title) {
    this.title = title;
  }
}
