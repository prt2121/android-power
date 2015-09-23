package com.prt2121.githubsdk;

import android.os.Parcel;
import android.os.Parcelable;
import com.prt2121.githubsdk.model.response.Repo;
import com.prt2121.githubsdk.model.response.ShaUrl;
import com.prt2121.githubsdk.model.response.User;

/**
 * Created by Bernat on 30/05/2015.
 */
public class Head extends ShaUrl {

  public static final Parcelable.Creator<Head> CREATOR = new Parcelable.Creator<Head>() {
    @Override public Head createFromParcel(Parcel in) {
      return new Head(in);
    }

    @Override public Head[] newArray(int size) {
      return new Head[size];
    }
  };
  public String ref;
  public Repo repo;
  public String label;
  public User user;

  public Head(Parcel in) {
    super(in);
    ref = in.readString();
    repo = in.readParcelable(Repo.class.getClassLoader());
    label = in.readString();
    user = in.readParcelable(User.class.getClassLoader());
  }

  public Head() {
    super();
  }

  @Override public int describeContents() {
    return super.describeContents();
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(ref);
    dest.writeParcelable(repo, flags);
    dest.writeString(label);
    dest.writeParcelable(user, flags);
  }
}
