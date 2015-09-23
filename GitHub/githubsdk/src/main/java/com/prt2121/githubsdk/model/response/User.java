package com.prt2121.githubsdk.model.response;

import android.os.Parcel;
import android.os.Parcelable;

public class User extends Organization {

  @SuppressWarnings("unused") public static final Parcelable.Creator<User> CREATOR =
      new Parcelable.Creator<User>() {
        @Override public User createFromParcel(Parcel in) {
          return new User(in);
        }

        @Override public User[] newArray(int size) {
          return new User[size];
        }
      };
  public boolean hireable;
  public String date;
  public String followers_url;
  public String following_url;
  public String gists_url;
  public String starred_url;
  public String subscriptions_url;
  public String organizations_url;
  public String repos_url;
  public String events_url;
  public String received_events_url;
  public int private_gists;
  public int owned_public_repos;
  public int owned_private_repos;
  public int total_public_repos;
  public int total_private_repos;
  public int collaborators;
  public int disk_usage;
  public UserPlan plan;

  public User() {
    super();
  }

  protected User(Parcel in) {
    super(in);
    hireable = in.readByte() != 0x00;
    followers_url = in.readString();
    following_url = in.readString();
    gists_url = in.readString();
    starred_url = in.readString();
    subscriptions_url = in.readString();
    organizations_url = in.readString();
    repos_url = in.readString();
    events_url = in.readString();
    date = in.readString();
    received_events_url = in.readString();
    private_gists = in.readInt();
    owned_public_repos = in.readInt();
    owned_private_repos = in.readInt();
    total_public_repos = in.readInt();
    total_private_repos = in.readInt();
    collaborators = in.readInt();
    disk_usage = in.readInt();
    plan = in.readParcelable(UserPlan.class.getClassLoader());
  }

  @Override public int describeContents() {
    return super.describeContents();
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeByte((byte) (hireable ? 0x01 : 0x00));
    dest.writeString(followers_url);
    dest.writeString(following_url);
    dest.writeString(gists_url);
    dest.writeString(starred_url);
    dest.writeString(subscriptions_url);
    dest.writeString(organizations_url);
    dest.writeString(repos_url);
    dest.writeString(events_url);
    dest.writeString(date);
    dest.writeString(received_events_url);
    dest.writeInt(private_gists);
    dest.writeInt(owned_public_repos);
    dest.writeInt(owned_private_repos);
    dest.writeInt(total_public_repos);
    dest.writeInt(total_private_repos);
    dest.writeInt(collaborators);
    dest.writeInt(disk_usage);
    dest.writeParcelable(plan, flags);
  }

  @Override public String toString() {
    final StringBuilder sb = new StringBuilder("User{");
    sb.append("id=").append(id);
    sb.append(", login='").append(login).append('\'');
    sb.append(", name='").append(name).append('\'');
    sb.append(", company='").append(company).append('\'');
    sb.append(", created_at=").append(created_at);
    sb.append(", updated_at=").append(updated_at);
    sb.append(", hireable=").append(hireable);
    sb.append(", avatar_url='").append(avatar_url).append('\'');
    sb.append(", gravatar_id='").append(gravatar_id).append('\'');
    sb.append(", blog='").append(blog).append('\'');
    sb.append(", bio='").append(bio).append('\'');
    sb.append(", email='").append(email).append('\'');
    sb.append(", location='").append(location).append('\'');
    sb.append(", html_url='").append(html_url).append('\'');
    sb.append(", followers_url='").append(followers_url).append('\'');
    sb.append(", following_url='").append(following_url).append('\'');
    sb.append(", gists_url='").append(gists_url).append('\'');
    sb.append(", starred_url='").append(starred_url).append('\'');
    sb.append(", subscriptions_url='").append(subscriptions_url).append('\'');
    sb.append(", organizations_url='").append(organizations_url).append('\'');
    sb.append(", repos_url='").append(repos_url).append('\'');
    sb.append(", events_url='").append(events_url).append('\'');
    sb.append(", received_events_url='").append(received_events_url).append('\'');
    sb.append(", type=").append(type);
    sb.append(", site_admin=").append(site_admin);
    sb.append(", public_repos=").append(public_repos);
    sb.append(", public_gists=").append(public_gists);
    sb.append(", owned_public_repos=").append(owned_public_repos);
    sb.append(", total_public_repos=").append(total_public_repos);
    sb.append(", followers=").append(followers);
    sb.append(", following=").append(following);
    sb.append(", contributors=").append(collaborators);
    sb.append(", disk_usage=").append(disk_usage);
    sb.append(", plan=").append(plan);
    sb.append('}');
    return sb.toString();
  }
}