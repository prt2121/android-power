package com.prt2121.contact

/**
 * Created by pt2121 on 1/29/16.
 */
import android.net.Uri
import kotlinslang.control.Option

data class Contact(
    val id: Long,
    val name: String,
    val profilePic: Option<Uri>,
    val phoneNumber: Option<String>
)