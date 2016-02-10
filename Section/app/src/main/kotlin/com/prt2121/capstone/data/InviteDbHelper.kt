package com.prt2121.capstone.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

/**
 * Created by pt2121 on 2/9/16.
 */
class InviteDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

  override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    p0?.execSQL("DROP TABLE IF EXISTS " + InviteEntry.TABLE_NAME)
    p0?.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME)
    onCreate(p0)
  }

  override fun onCreate(p0: SQLiteDatabase?) {
    p0?.execSQL(SQL_CREATE_USER_TABLE)
    p0?.execSQL(SQL_CREATE_INVITE_TABLE)
  }

  companion object {
    val DATABASE_NAME = "invite.db"
    val SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        UserEntry.COLUMN_FIRST_NAME + " TEXT, " +
        UserEntry.COLUMN_LAST_NAME + " TEXT, " +
        UserEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " +
        UserEntry.COLUMN_PHOTO_URI + " TEXT " +
        " );"

    val SQL_CREATE_INVITE_TABLE = "CREATE TABLE " + InviteEntry.TABLE_NAME + " ( " +
        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

        InviteEntry.COLUMN_BACKEND_ID + " INTEGER NOT NULL, " +

        InviteEntry.COLUMN_FROM_ID + " INTEGER NOT NULL, " +
        InviteEntry.COLUMN_TO_ID + " INTEGER NOT NULL, " +

        InviteEntry.COLUMN_DESTINATION_LATLNG + " TEXT NOT NULL," +
        InviteEntry.COLUMN_DESTINATION_ADDRESS + " TEXT NOT NULL, " +
        InviteEntry.COLUMN_MESSAGE + " TEXT, " +
        InviteEntry.COLUMN_STATUS + " TEXT, " +
        InviteEntry.COLUMN_PICKUP_ADDRESS + " TEXT, " +
        InviteEntry.COLUMN_CREATE_AT + " INTEGER NOT NULL, " +
        " FOREIGN KEY (" + InviteEntry.COLUMN_FROM_ID + ") REFERENCES " +
        UserEntry.TABLE_NAME + " (" + BaseColumns._ID + "), " +

        " FOREIGN KEY (" + InviteEntry.COLUMN_TO_ID + ") REFERENCES " +
        UserEntry.TABLE_NAME + " (" + BaseColumns._ID + ") " +
        " );"
  }

}