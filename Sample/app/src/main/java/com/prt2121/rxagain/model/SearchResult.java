package com.prt2121.rxagain.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class SearchResult {

  @SerializedName("total_count") @Expose private Integer totalCount;
  @SerializedName("incomplete_results") @Expose private Boolean incompleteResults;
  @SerializedName("items") @Expose private List<Repo> repos = new ArrayList<Repo>();

  /**
   * @return The totalCount
   */
  public Integer getTotalCount() {
    return totalCount;
  }

  /**
   * @param totalCount The total_count
   */
  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  /**
   * @return The incompleteResults
   */
  public Boolean getIncompleteResults() {
    return incompleteResults;
  }

  /**
   * @param incompleteResults The incomplete_results
   */
  public void setIncompleteResults(Boolean incompleteResults) {
    this.incompleteResults = incompleteResults;
  }

  /**
   * @return The repos
   */
  public List<Repo> getRepos() {
    return repos;
  }

  /**
   * @param repos The repos
   */
  public void setRepos(List<Repo> repos) {
    this.repos = repos;
  }
}
