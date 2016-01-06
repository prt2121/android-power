package com.prt2121.contentprovider

import android.content.ContentResolver
import android.content.UriMatcher
import android.net.Uri

/**
 * Created by pt2121 on 1/6/16.
 */
object InviteContract {
  val AUTHORITY = "com.prt2121.summon.provider"
  val BASE_PATH = "invite"
  val INVITE_DIR = 10
  val INVITE_ITEM = 20
  val CONTENT_URI = Uri.parse("content://$AUTHORITY/$BASE_PATH")
  val DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/invites"
  val ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/invite"
  val MATCHER = UriMatcher(UriMatcher.NO_MATCH)

  init {
    MATCHER.addURI(AUTHORITY, BASE_PATH, INVITE_DIR)
    MATCHER.addURI(AUTHORITY, BASE_PATH + "/#", INVITE_ITEM)
  }
}