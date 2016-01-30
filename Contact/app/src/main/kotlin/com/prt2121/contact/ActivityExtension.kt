package com.prt2121.contact

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

/**
 * Created by pt2121 on 1/29/16.
 */
fun AppCompatActivity.requestPermission(permission: String, showRationale: () -> Unit) {
  if (permission !in Perm.appPermissions) {
    Perm.appPermissions.add(permission)
  }
  if (ContextCompat.checkSelfPermission(this, permission) !== PackageManager.PERMISSION_GRANTED) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
      showRationale()
    } else {
      ActivityCompat.requestPermissions(this, arrayOf(permission), Perm.appPermissions.indexOf(permission))
    }
  }
}
