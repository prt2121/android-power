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
import com.invite.Invite
import com.invite.Status
import com.invite.User
import org.funktionale.option.Option
import org.funktionale.option.getOrElse
import org.funktionale.option.toOption

/**
 * Created by pt2121 on 2/9/16.
 */
class InviteProvider : ContentProvider() {

  private val INVITE = 100
  private val INVITE_ME = 110
  private val INVITE_WITH_ID = 120
  private val USER = 200

  private val uriMatcher = buildUriMatcher()
  private var dbHelper: InviteDbHelper? = null
  private val queryBuilder = SQLiteQueryBuilder()

  private val inviteSelection = InviteEntry.TABLE_NAME + "." + BaseColumns._ID + " = ? "

  init {
    // invite
    // inner join user u1
    // on invite.to_id = u1._id
    // inner join user u2
    // on invite.to_id = u2._id
    queryBuilder.tables = InviteEntry.TABLE_NAME +
        " INNER JOIN " + UserEntry.TABLE_NAME + " u1 " +
        " ON " + InviteEntry.TABLE_NAME + "." + InviteEntry.COLUMN_TO_ID + " = u1." + BaseColumns._ID +
        " INNER JOIN " + UserEntry.TABLE_NAME + " u2 " +
        " ON " + InviteEntry.TABLE_NAME + "." + InviteEntry.COLUMN_FROM_ID + " = u2." + BaseColumns._ID
  }

  fun getInvites(projection: Array<String>?, sortOrder: String?): Option<Cursor> =
      dbHelper.toOption().map {
        queryBuilder.query(dbHelper?.readableDatabase,
            projection,
            null,
            null,
            null,
            null,
            sortOrder)
      }

  fun getInvite(uri: Uri, projection: Array<String>?, sortOrder: String?): Option<Cursor> =
      dbHelper.toOption().map {
        queryBuilder.query(dbHelper?.readableDatabase,
            projection,
            inviteSelection,
            arrayOf(InviteEntry.getInviteIdFromUri(uri)),
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

      INVITE_ME -> getInvites(projection, sortOrder).orNull()

      INVITE_WITH_ID -> getInvite(uri, projection, sortOrder).orNull()

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
        INVITE_ME -> InviteEntry.CONTENT_TYPE
        INVITE_WITH_ID -> InviteEntry.CONTENT_ITEM_TYPE
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
    matcher.addURI(authority, InviteContract.PATH_INVITE_FROM_ME, INVITE_ME)
    matcher.addURI(authority, InviteContract.PATH_INVITE + "/#", INVITE_WITH_ID)
    matcher.addURI(authority, InviteContract.PATH_USER, USER)
    return matcher
  }

  companion object {
    fun inviteFromCursor(cursor: Cursor): Invite {
      val statusStr = cursor.getString(cursor.getColumnIndex(InviteEntry.COLUMN_STATUS))
      val status = when (statusStr) {
        "ACCEPT" -> Status.ACCEPT
        "PENDING" -> Status.PENDING
        "REJECT" -> Status.REJECT
        else -> Status.CANCEL
      }
      val invite = Invite(
          cursor.getInt(QueryColumn.inviteId).toString(),
          User(cursor.getString(QueryColumn.toFirstName),
              cursor.getString(QueryColumn.toLastName),
              cursor.getString(QueryColumn.toPhoneNumber),
              cursor.getString(QueryColumn.toPhotoUri)),
          User(cursor.getString(QueryColumn.fromFirstName),
              cursor.getString(QueryColumn.fromLastName),
              cursor.getString(QueryColumn.fromPhoneNumber),
              cursor.getString(QueryColumn.fromPhotoUri)),
          cursor.getString(QueryColumn.destLatLng),
          cursor.getString(QueryColumn.destAddress),
          cursor.getString(QueryColumn.message),
          status,
          cursor.getString(QueryColumn.pickupAddress),
          cursor.getLong(QueryColumn.createAt)
      )
      return invite
    }
  }

  object QueryColumn {
    // query column mapping
    const val inviteId = 0
    const val backendId = 1
    const val fromId = 2
    const val toId = 3
    const val destLatLng = 4
    const val destAddress = 5
    const val message = 6
    const val status = 7
    const val pickupAddress = 8
    const val createAt = 9
    const val fromUserId = 10 // same as fromId
    const val fromFirstName = 11
    const val fromLastName = 12
    const val fromPhoneNumber = 13
    const val fromPhotoUri = 14
    const val toUserId = 15
    const val toFirstName = 16
    const val toLastName = 17
    const val toPhoneNumber = 18
    const val toPhotoUri = 19
  }

}