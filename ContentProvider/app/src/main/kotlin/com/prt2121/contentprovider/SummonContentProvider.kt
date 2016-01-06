package com.prt2121.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import com.prt2121.contentprovider.InviteContract.BASE_PATH
import com.prt2121.contentprovider.InviteContract.INVITE_DIR
import com.prt2121.contentprovider.InviteContract.INVITE_ITEM
import com.prt2121.contentprovider.InviteContract.MATCHER

/**
 * Created by pt2121 on 1/6/16.
 */
open class SummonContentProvider() : ContentProvider() {

  var helper: DbHelper? = null

  override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
    throw UnsupportedOperationException()
  }

  override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
    val db = helper?.writableDatabase
    val qb = SQLiteQueryBuilder()
    qb.tables = InviteEntry.TABLE_NAME
//    val uriType = MATCHER.match(uri)
//    if (uriType == INVITE_ITEM) {
      //qb.appendWhere("${InviteEntry.INVITE_ID}=${uri?.lastPathSegment}")
//      qb.appendWhere("${InviteEntry._ID}=${uri?.lastPathSegment}")
//    }
    val cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder)
    cursor.setNotificationUri(context.contentResolver, uri)
    return cursor
  }

  override fun onCreate(): Boolean {
    helper = DbHelper(context)
    return helper != null
  }

  override fun getType(uri: Uri?): String? = null

  override fun insert(uri: Uri?, values: ContentValues?): Uri? {
    val uriType = MATCHER.match(uri)
    val db = helper?.writableDatabase
    val id = if (uriType == INVITE_DIR) db?.insert(InviteEntry.TABLE_NAME, null, values) else 0
    context.contentResolver.notifyChange(uri, null)
    return Uri.parse(BASE_PATH + "/" + id)
  }

  override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
    val uriType = MATCHER.match(uri)
    val db = helper?.writableDatabase
    val row = if (uriType == INVITE_ITEM) {
      db?.update(InviteEntry.TABLE_NAME, values, "${InviteEntry.INVITE_ID}=${uri?.lastPathSegment}", null) ?: 0
    } else 0
    context.contentResolver.notifyChange(uri, null)
    return row
  }

}