package com.prt2121.githubsdk.model.response;

import android.os.Parcel;
import android.os.Parcelable;

public class GitReferenceEntry extends ShaUrl {
  public static final Parcelable.Creator<GitReferenceEntry> CREATOR =
      new Parcelable.Creator<GitReferenceEntry>() {
        @Override public GitReferenceEntry createFromParcel(Parcel in) {
          return new GitReferenceEntry(in);
        }

        @Override public GitReferenceEntry[] newArray(int size) {
          return new GitReferenceEntry[size];
        }
      };
  public String type;

  protected GitReferenceEntry(Parcel in) {
    super(in);
    type = in.readString();
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(type);
  }
}
