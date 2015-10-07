package com.prt2121.github;

/**
 * Lifecycle events that can be emitted by Activities.
 *
 * borrowed from trello/RxLifecycle
 */
public enum ActivityEvent {

  CREATE,
  START,
  RESUME,
  PAUSE,
  STOP,
  DESTROY

}