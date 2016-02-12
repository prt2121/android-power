package com.prt2121.capstone.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by pt2121 on 2/12/16.
 */
class AuthenticatorService : Service() {

  private var authenticator: Authenticator? = null

  override fun onCreate() {
    authenticator = Authenticator(this)
  }

  override fun onBind(intent: Intent): IBinder? = authenticator!!.iBinder
}