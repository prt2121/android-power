package com.prt2121.contact

import android.content.pm.PackageManager
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by pt2121 on 1/29/16.
 */
object Perm {
  var appPermissions = ConcurrentLinkedQueue<String>()

  fun verifyPermissions(grantResults: IntArray): Boolean {
    // At least one result must be checked.
    if (grantResults.size < 1) {
      return false
    }
    // Verify that each required permission has been granted, otherwise return false.
    return grantResults.all { it == PackageManager.PERMISSION_GRANTED }
  }
}