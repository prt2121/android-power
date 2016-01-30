package com.prt2121.contact

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import com.prt2121.contact.PermissionResult.SHOW_RATIONALE
import rx.Observable

/**
 * Created by pt2121 on 1/29/16.
 */
val AppCompatActivity.rootView: View
  get() = (findViewById(android.R.id.content) as ViewGroup).getChildAt(0)

fun AppCompatActivity.requestPermissionEvents(permission: String): Observable<PermissionEvent> {
  if (ContextCompat.checkSelfPermission(this, permission) !== PackageManager.PERMISSION_GRANTED) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
      Perm.subject.onNext(PermissionEvent(permission, SHOW_RATIONALE))
    } else {
      requestPermission(permission)
    }
  }
  return Perm.subject
}

fun AppCompatActivity.requestPermission(permission: String) {
  ActivityCompat.requestPermissions(this, arrayOf(permission), Perm.CODE)
}