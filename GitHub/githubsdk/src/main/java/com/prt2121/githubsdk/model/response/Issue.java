package com.prt2121.githubsdk.model.response;

import android.os.Parcel;
import com.google.gson.annotations.SerializedName;
import com.prt2121.githubsdk.PullRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 20/07/2014.
 */

public class Issue extends GithubComment {

  public static final Creator<Issue> CREATOR = new Creator<Issue>() {
    @Override public Issue createFromParcel(Parcel in) {
      return new Issue(in);
    }

    @Override public Issue[] newArray(int size) {
      return new Issue[size];
    }
  };
  public int number;
  public IssueState state;
  public boolean locked;
  public String title;
  public List<Label> labels;
  public User assignee;
  public Milestone milestone;
  public int comments;
  @SerializedName("pull_request") public PullRequest pullRequest;
  @SerializedName("closed_at") public String closedAt;
  public Repo repository;

  public Issue(Parcel in) {
    super(in);
    number = in.readInt();
    try {
      state = IssueState.valueOf(in.readString());
    } catch (IllegalArgumentException x) {
      state = null;
    }
    locked = in.readByte() != 0x00;
    title = in.readString();
    labels = new ArrayList<>();
    in.readTypedList(labels, Label.CREATOR);
    assignee = in.readParcelable(User.class.getClassLoader());
    milestone = in.readParcelable(Milestone.class.getClassLoader());
    comments = in.readInt();
    pullRequest = in.readParcelable(PullRequest.class.getClassLoader());
    closedAt = in.readString();
    repository = in.readParcelable(Repo.class.getClassLoader());
  }

  public Issue() {
    super();
  }

  @Override public int describeContents() {
    return super.describeContents();
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeInt(number);
    dest.writeString(state != null ? state.toString() : "");
    dest.writeByte((byte) (locked ? 0x01 : 0x00));
    dest.writeString(title);
    dest.writeTypedList(labels);
    dest.writeParcelable(assignee, flags);
    dest.writeParcelable(milestone, flags);
    dest.writeInt(comments);
    dest.writeParcelable(pullRequest, flags);
    dest.writeString(closedAt);
    dest.writeParcelable(repository, flags);
  }
}
