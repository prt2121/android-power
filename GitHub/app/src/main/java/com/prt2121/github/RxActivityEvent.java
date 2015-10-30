package com.prt2121.github;

/**
 * Lifecycle events that can be emitted by Activities.
 *
 * borrowed from trello/RxLifecycle
 */
public enum RxActivityEvent {

  CREATE("CREATE"),
  START("START"),
  RESUME("RESUME"),
  PAUSE("PAUSE"),
  STOP("STOP"),
  DESTROY("DESTROY");

  private final String name;

  private RxActivityEvent(String s) {
    name = s;
  }

  public boolean equalsName(String otherName) {
    return (otherName != null) && name.equals(otherName);
  }

  @Override public String toString() {
    return this.name;
  }

}