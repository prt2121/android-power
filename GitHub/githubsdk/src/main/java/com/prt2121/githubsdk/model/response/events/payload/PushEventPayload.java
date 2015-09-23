package com.prt2121.githubsdk.model.response.events.payload;

import com.prt2121.githubsdk.model.response.Commit;
import java.util.List;

/**
 * Created by Bernat on 03/10/2014.
 */
public class PushEventPayload extends GithubEventPayload {
  public long push_id;
  public int size;
  public int distinct_size;
  public String ref;
  public String head;
  public String before;
  public List<Commit> commits;
}
