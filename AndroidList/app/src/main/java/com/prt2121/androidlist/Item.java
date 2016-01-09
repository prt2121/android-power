package com.prt2121.androidlist;

/**
 * Created by pt2121 on 1/9/16.
 */
public class Item {
  private String title;
  private String description;

  public Item(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Item item = (Item) o;

    if (title != null ? !title.equals(item.title) : item.title != null) return false;
    return description != null ? description.equals(item.description) : item.description == null;
  }

  @Override public int hashCode() {
    int result = title != null ? title.hashCode() : 0;
    result = 31 * result + (description != null ? description.hashCode() : 0);
    return result;
  }

  @Override public String toString() {
    return "Item{" +
        "title='" + title + '\'' +
        ", description='" + description + '\'' +
        '}';
  }
}
