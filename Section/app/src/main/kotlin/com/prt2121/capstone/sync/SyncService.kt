package com.prt2121.capstone.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SyncService : Service() {

  override fun onCreate() {
    synchronized (sSyncAdapterLock) {
      if (sSunshineSyncAdapter == null) {
        sSunshineSyncAdapter = InviteSyncAdapter(applicationContext, true)
      }
    }
  }

  override fun onBind(intent: Intent): IBinder? {
    return sSunshineSyncAdapter!!.syncAdapterBinder
  }

  companion object {
    private val sSyncAdapterLock = Object()
    private var sSunshineSyncAdapter: InviteSyncAdapter? = null
  }
}