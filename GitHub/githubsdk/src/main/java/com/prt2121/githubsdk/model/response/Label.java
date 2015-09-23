package com.prt2121.githubsdk.model.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 22/08/2014.
 */
public class Label extends ShaUrl {
  public static final Parcelable.Creator<Label> CREATOR = new Parcelable.Creator<Label>() {
    @Override public Label createFromParcel(Parcel in) {
      return new Label(in);
    }

    @Override public Label[] newArray(int size) {
      return new Label[size];
    }
  };
  public String name;
  public String color;

  protected Label(Parcel in) {
    super(in);
    name = in.readString();
    color = in.readString();
  }

  public Label() {
    super();
  }

  @Override public int describeContents() {
    return super.describeContents();
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(name);
    dest.writeString(color);
  }
}
