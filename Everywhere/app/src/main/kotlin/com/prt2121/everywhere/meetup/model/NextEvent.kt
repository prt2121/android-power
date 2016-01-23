package com.prt2121.everywhere.meetup.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by pt2121 on 1/18/16.
 */
data class NextEvent(
    @SerializedName("id")
    @Expose
    var id: String,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("yes_rsvp_count")
    @Expose
    var yesRsvpCount: Int,
    @SerializedName("time")
    @Expose
    var time: Long,
    @SerializedName("utc_offset")
    @Expose
    var utcOffset: Int
)