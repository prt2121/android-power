package com.prt2121.capstone.data

import android.annotation.TargetApi
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.provider.BaseColumns
import org.funktionale.option.Option
import org.funktionale.option.getOrElse
import org.funktionale.option.toOption

/**
 * Created by pt2121 on 2/9/16.
 */
class InviteProvider : ContentProvider() {

  private val INVITE = 100
  private val USER = 200

  private val uriMatcher = buildUriMatcher()
  private var dbHelper: InviteDbHelper? = null
  private val queryBuilder = SQLiteQueryBuilder()

  init {
    queryBuilder.tables = InviteEntry.TABLE_NAME + " INNER JOIN " +
        UserEntry.TABLE_NAME +
        " ON " + InviteEntry.TABLE_NAME +
        "." + InviteEntry.COLUMN_TO_ID +
        " = " + InviteEntry.TABLE_NAME +
        "." + BaseColumns._ID
  }

  private fun getInvites(projection: Array<String>, sortOrder: String): Option<Cursor> =
      dbHelper.toOption().map {
        queryBuilder.query(dbHelper?.readableDatabase,
            projection,
            null,
            null,
            null,
            null,
            sortOrder)
      }

  override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
    val db = dbHelper?.writableDatabase
    val match = uriMatcher.match(uri)

    val p1 = selection ?: "1"
    val rowsDeleted = when (match) {
      INVITE -> db?.delete(InviteEntry.TABLE_NAME, p1, selectionArgs).toOption()
      USER -> db?.delete(UserEntry.TABLE_NAME, p1, selectionArgs).toOption()
      else -> throw UnsupportedOperationException("Unknown uri: " + uri)
    }
    rowsDeleted.filterNot { it <= 0 }
        .forEach {
          context!!.contentResolver.notifyChange(uri, null)
        }
    return rowsDeleted.getOrElse { 0 }
  }

  override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
    val cursor = when (uriMatcher.match(uri)) {
      INVITE -> dbHelper?.readableDatabase?.query(
          InviteEntry.TABLE_NAME,
          projection,
          selection,
          selectionArgs,
          null,
          null,
          sortOrder)
      USER -> dbHelper?.readableDatabase?.query(
          UserEntry.TABLE_NAME,
          projection,
          selection,
          selectionArgs,
          null,
          null,
          sortOrder)
      else -> throw UnsupportedOperationException("Unknown uri: " + uri)
    }
    cursor?.setNotificationUri(context!!.contentResolver, uri)
    return cursor
  }

  override fun onCreate(): Boolean {
    dbHelper = InviteDbHelper(context)
    return true
  }

  override fun getType(uri: Uri?): String? =
      when (uriMatcher.match(uri)) {
        INVITE -> InviteEntry.CONTENT_TYPE
        USER -> UserEntry.CONTENT_TYPE
        else -> throw UnsupportedOperationException("Unknown uri: " + uri)
      }

  override fun insert(uri: Uri?, values: ContentValues?): Uri? {
    val returnUri =
        when (uriMatcher.match(uri)) {
          INVITE -> {
            val id = dbHelper?.writableDatabase?.insert(
                InviteEntry.TABLE_NAME,
                null,
                values) ?: -1
            if (id > 0) InviteEntry.buildUri(id)
            else throw SQLException("Failed to insert row into " + uri)
          }
          USER -> {
            val id = dbHelper?.writableDatabase?.insert(
                UserEntry.TABLE_NAME,
                null,
                values) ?: -1
            if (id > 0) UserEntry.buildUri(id)
            else throw SQLException("Failed to insert row into " + uri)
          }
          else -> throw UnsupportedOperationException("Unknown uri: " + uri)
        }
    context!!.contentResolver.notifyChange(uri, null)
    return returnUri
  }

  override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
    val db = dbHelper?.writableDatabase
    val match = uriMatcher.match(uri)
    val rowsUpdated = when (match) {
      INVITE -> db?.update(InviteEntry.TABLE_NAME, values, selection, selectionArgs)
      USER -> db?.update(UserEntry.TABLE_NAME, values, selection, selectionArgs)
      else -> throw UnsupportedOperationException("Unknown uri: " + uri)
    }.toOption()
    rowsUpdated.filterNot { it <= 0 }
        .forEach {
          context!!.contentResolver.notifyChange(uri, null)
        }
    return rowsUpdated.getOrElse { 0 }
  }

  override fun bulkInsert(uri: Uri?, values: Array<out ContentValues>?): Int {
    val db = dbHelper?.writableDatabase
    val match = uriMatcher.match(uri)
    when (match) {
      INVITE -> {
        db?.beginTransaction()
        val returnCount =
            try {
              val c =
                  values.orEmpty()
                      .fold(0) { acc, n ->
                        val id = db?.insert(InviteEntry.TABLE_NAME, null, n) ?: -1
                        if (id >= 0) acc + 1
                        else acc
                      }
              db?.setTransactionSuccessful()
              c
            } finally {
              db?.endTransaction()
            }
        context.contentResolver.notifyChange(uri, null)
        return returnCount
      }
      else -> return super.bulkInsert(uri, values)
    }
  }

  @TargetApi(11)
  override fun shutdown() {
    dbHelper?.close()
    super.shutdown()
  }

  private fun buildUriMatcher(): UriMatcher {
    val matcher = UriMatcher(UriMatcher.NO_MATCH)
    val authority = InviteContract.CONTENT_AUTHORITY
    matcher.addURI(authority, InviteContract.PATH_INVITE, INVITE)
    matcher.addURI(authority, InviteContract.PATH_USER, USER)
    return matcher
  }

}