package com.prt2121.perm

import android.R
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import com.prt2121.perm.PermissionResult.SHOW_RATIONALE

/**
 * Created by pt2121 on 1/29/16.
 */
val AppCompatActivity.rootView: View
  get() = (findViewById(R.id.content) as ViewGroup).getChildAt(0)

fun AppCompatActivity.requestPermissionEvents(vararg ps: String): rx.Observable<PermissionEvent> {
  val granted = ps.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }

  val ls = granted.partition { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }

  ls.first.forEach { Perm.subject.onNext(PermissionEvent(it, SHOW_RATIONALE)) }
  if (ls.second.isNotEmpty()) askPermission(*ls.second.toTypedArray())

  return Perm.subject
}

fun AppCompatActivity.askPermission(vararg permissions: String) {
  ActivityCompat.requestPermissions(this, permissions, Perm.CODE)
}