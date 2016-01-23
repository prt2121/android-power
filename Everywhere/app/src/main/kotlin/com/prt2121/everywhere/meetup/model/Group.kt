package com.prt2121.everywhere.meetup.model

/**
 * Created by pt2121 on 1/18/16.
 */
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Group(
    @SerializedName("score")
    @Expose
    var score: Double,
    @SerializedName("id")
    @Expose
    var id: Int,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("link")
    @Expose
    var link: String,
    @SerializedName("urlname")
    @Expose
    var urlname: String,
    @SerializedName("description")
    @Expose
    var description: String,
    @SerializedName("created")
    @Expose
    var created: Long,
    @SerializedName("city")
    @Expose
    var city: String,
    @SerializedName("country")
    @Expose
    var country: String,
    @SerializedName("state")
    @Expose
    var state: String,
    @SerializedName("join_mode")
    @Expose
    var joinMode: String,
    @SerializedName("visibility")
    @Expose
    var visibility: String,
    @SerializedName("lat")
    @Expose
    var lat: Double,
    @SerializedName("lon")
    @Expose
    var lon: Double,
    @SerializedName("members")
    @Expose
    var members: Int,
    @SerializedName("organizer")
    @Expose
    var organizer: Organizer,
    @SerializedName("who")
    @Expose
    var who: String,
    @SerializedName("group_photo")
    @Expose
    var groupPhoto: GroupPhoto,
    @SerializedName("timezone")
    @Expose
    var timezone: String,
    @SerializedName("next_event")
    @Expose
    var nextEvent: NextEvent,
    @SerializedName("category")
    @Expose
    var category: Category,
    @SerializedName("photos")
    @Expose
    var photos: List<GroupPhoto> = emptyList()
)