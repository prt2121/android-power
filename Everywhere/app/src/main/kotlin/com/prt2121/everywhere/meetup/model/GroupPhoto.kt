package com.prt2121.everywhere.meetup.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by pt2121 on 1/18/16.
 */
data class GroupPhoto(
    @SerializedName("id")
    @Expose
    var id: Int,
    @SerializedName("highres_link")
    @Expose
    var highresLink: String,
    @SerializedName("photo_link")
    @Expose
    var photoLink: String,
    @SerializedName("thumb_link")
    @Expose
    var thumbLink: String
    )