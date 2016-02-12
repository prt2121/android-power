package com.invite

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by pt2121 on 12/22/15.
 */
data class Invite(

    @SerializedName("id") @Expose
    val id: String? = null,

    @SerializedName("from") @Expose
    val from: User,

    @SerializedName("to") @Expose
    val to: User,

    @SerializedName("destinationLatLng") @Expose
    val destinationLatLng: String,

    @SerializedName("destinationAddress") @Expose
    val destinationAddress: String,

    @SerializedName("message") @Expose
    val message: String,

    @SerializedName("status") @Expose
    val status: Status = Status.PENDING,

    @SerializedName("pickupAddress") @Expose
    val pickupAddress: String = "",

    @SerializedName("createAt") @Expose
    val createAt: Long
) {
  fun isActive(): Boolean = System.currentTimeMillis() - createAt < 60 * 60 * 1000 // 1 hour
  fun readableCreateAt() : String = SimpleDateFormat("MM/dd/yy hh:mm a").format(Date(createAt))
}

enum class Status {
  PENDING, ACCEPT, REJECT, CANCEL
}

data class User(

    @SerializedName("firstName") @Expose
    val firstName: String,

    @SerializedName("lastName") @Expose
    val lastName: String,

    @SerializedName("phoneNumber") @Expose
    val phoneNumber: String,

    @SerializedName("photoUri") @Expose
    val photoUri: String
)