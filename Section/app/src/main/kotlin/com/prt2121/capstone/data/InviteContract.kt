package com.prt2121.capstone.data

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns
import com.prt2121.capstone.data.InviteContract.BASE_CONTENT_URI
import com.prt2121.capstone.data.InviteContract.CONTENT_AUTHORITY
import com.prt2121.capstone.data.InviteContract.PATH_INVITE
import com.prt2121.capstone.data.InviteContract.PATH_USER

/**
 * Created by pt2121 on 2/9/16.
 *
 * Defines table and column names for the invite database.
 */
object InviteContract {
  const val CONTENT_AUTHORITY = "com.prt2121.capstone"
  val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")
  const val PATH_INVITE = "invite"
  const val PATH_USER = "user"
}

object InviteEntry : BaseColumns {
  val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVITE).build()
  const val CONTENT_TYPE =
      ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVITE
  const val CONTENT_ITEM_TYPE =
      ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVITE

  const val TABLE_NAME = "invite"

  const val COLUMN_BACKEND_ID = "id"
  // from user id
  const val COLUMN_FROM_ID = "from_id"
  // to user id
  const val COLUMN_TO_ID = "to_id"
  const val COLUMN_DESTINATION_LATLNG = "destination_latlng"
  const val COLUMN_DESTINATION_ADDRESS = "destination_address"
  const val COLUMN_MESSAGE = "message"
  const val COLUMN_STATUS = "status"
  const val COLUMN_PICKUP_ADDRESS = "pickup_address"
  const val COLUMN_CREATE_AT = "create_at"

  fun buildUri(id: Long): Uri = ContentUris.withAppendedId(CONTENT_URI, id)

  fun buildUriWithFrom(from: String): Uri = CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_FROM_ID, from).build()
}

object UserEntry : BaseColumns {
  val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build()
  const val CONTENT_TYPE =
      ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER
  const val CONTENT_ITEM_TYPE =
      ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER

  const val TABLE_NAME = "user"
  const val COLUMN_FIRST_NAME = "firstName"
  const val COLUMN_LAST_NAME = "lastName"
  const val COLUMN_PHONE_NUMBER = "phoneNumber"
  const val COLUMN_PHOTO_URI = "photoUri"

  fun buildUri(id: Long): Uri = ContentUris.withAppendedId(CONTENT_URI, id)
}