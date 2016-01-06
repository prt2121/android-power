package com.prt2121.contentprovider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by pt2121 on 1/6/16.
 */
class DbHelper(context: Context) : SQLiteOpenHelper(context, "summon.db", null, 1) {

  val SQL_CREATE =
      "CREATE TABLE " + InviteEntry.TABLE_NAME + " (" +
          InviteEntry._ID + " INTEGER PRIMARY KEY, " +
          InviteEntry.INVITE_ID + " TEXT UNIQUE, " +
          InviteEntry.TO_USER + " TEXT, " +
          InviteEntry.TO_USER_PROFILE_IMAGE_URI + " TEXT, " +
          InviteEntry.DESTINATION_LATLNG + " TEXT, " +
          InviteEntry.DESTINATION_ADDRESS + " TEXT, " +
          InviteEntry.MESSAGE + " TEXT, " +
          InviteEntry.STATUS + " TEXT, " +
          InviteEntry.PICKUP_ADDRESS + " TEXT " +
          " )";

  override fun onCreate(db: SQLiteDatabase?) {
    db?.execSQL(SQL_CREATE)
  }

  override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    db?.execSQL("drop table if exists " + InviteEntry.TABLE_NAME)
    onCreate(db)
  }

}