package com.prt2121.everywhere.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
/**
 * Created by pt2121 on 1/18/16.
 */
data class Category(
    @SerializedName("id")
    @Expose
    var id: Int,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("shortname")
    @Expose
    var shortname: String,
    @SerializedName("sort_name")
    @Expose
    var sortName: String
)