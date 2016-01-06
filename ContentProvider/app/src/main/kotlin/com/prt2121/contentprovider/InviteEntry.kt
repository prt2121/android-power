package com.prt2121.contentprovider

import android.provider.BaseColumns

/**
 * Created by pt2121 on 1/6/16.
 */
object InviteEntry : BaseColumns {
  val _ID = BaseColumns._ID
  val TABLE_NAME = "invite"
  val INVITE_ID = "inviteId"
  val TO_USER = "toUserName"
  val TO_USER_PROFILE_IMAGE_URI = "toUserProfileImageUri"
  val DESTINATION_LATLNG = "destinationLatLng"
  val DESTINATION_ADDRESS = "destinationAddress"
  val MESSAGE = "message"
  val STATUS = "status"
  val PICKUP_ADDRESS = "pickupAddress"
}