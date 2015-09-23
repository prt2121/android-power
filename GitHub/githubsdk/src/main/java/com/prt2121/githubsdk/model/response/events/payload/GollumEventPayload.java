package com.prt2121.githubsdk.model.response.events.payload;

import com.prt2121.githubsdk.model.response.GollumPage;
import java.util.List;

public class GollumEventPayload extends GithubEventPayload {

  public List<GollumPage> pages;
}
