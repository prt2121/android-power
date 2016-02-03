package com.prt2121.perm

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import com.prt2121.perm.PermissionResult.*
import kotlin.all
import kotlin.forEach
import kotlin.toTypedArray
import kotlin.zip

/**
 * Created by pt2121 on 1/29/16.
 */
object Perm {
  val CODE = 123
  val subject: rx.subjects.PublishSubject<PermissionEvent> = rx.subjects.PublishSubject.create()

  fun verifyPermissions(grantResults: IntArray): Boolean {
    // At least one result must be checked.
    if (grantResults.size < 1) {
      return false
    }
    // Verify that each required permission has been granted, otherwise return false.
    return grantResults.all { it == PackageManager.PERMISSION_GRANTED }
  }

  fun onResult(activity: AppCompatActivity, requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
    if (requestCode == CODE) {
      val permissionEvents = permissions.zip(grantResults.toTypedArray()) { p, r ->
        val type = if (Perm.verifyPermissions(grantResults)) GRANTED
        else {
          if (activity.shouldShowRequestPermissionRationale(p)) SHOW_RATIONALE
          else NEVER_ASK_AGAIN
        }
        PermissionEvent(p, type)
      }

      permissionEvents.forEach {
        subject.onNext(it)
      }
      return true
    } else {
      return false
    }
  }
}

data class PermissionEvent(val permission: String, val result: PermissionResult)

enum class PermissionResult {
  GRANTED, SHOW_RATIONALE, NEVER_ASK_AGAIN
}