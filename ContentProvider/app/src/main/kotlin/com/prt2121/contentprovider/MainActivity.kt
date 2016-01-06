package com.prt2121.contentprovider

import android.content.ContentValues
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

/**
 * Created by pt2121 on 1/6/16.
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val values = ContentValues()
    values.put(InviteEntry.INVITE_ID, "12349")
    values.put(InviteEntry.TO_USER, "prat")
    values.put(InviteEntry.TO_USER_PROFILE_IMAGE_URI, "uri")
    values.put(InviteEntry.DESTINATION_LATLNG, "pickup latlng")
    values.put(InviteEntry.DESTINATION_ADDRESS, "pickup address")
    values.put(InviteEntry.MESSAGE, "hello there")
    values.put(InviteEntry.STATUS, "PENDING")
    values.put(InviteEntry.PICKUP_ADDRESS, "pickup address")

    val uri = contentResolver.insert(InviteContract.CONTENT_URI, values)
    Log.v("MainActivity", "uri ${uri.toString()}")
    if(uri != null) {
      val cursor = contentResolver.query(InviteContract.CONTENT_URI, null, null, null, null)
      //val cursor = contentResolver.query(uri, null, null, null, null)
      if(cursor != null) {
        cursor.moveToFirst()
        val status = cursor.getString(cursor.getColumnIndexOrThrow(InviteEntry.STATUS))
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(InviteEntry._ID))
        Log.v("MainActivity", "id $id status $status")
      }
    }
  }
}