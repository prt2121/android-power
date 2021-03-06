package com.prt2121.capstone.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import com.invite.InviteApi
import com.invite.User
import com.prt2121.capstone.R
import com.prt2121.capstone.backoff
import com.prt2121.capstone.data.InviteEntry
import com.prt2121.capstone.data.InviteProvider.Companion.inviteExists
import com.prt2121.capstone.data.InviteProvider.Companion.userExists
import com.prt2121.capstone.data.UserEntry
import com.prt2121.capstone.data.toContentValues
import rx.Observable
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by pt2121 on 2/12/16.
 */
class InviteSyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {
  override fun onPerformSync(account: Account?,
                             extras: Bundle?,
                             authority: String?,
                             provider: ContentProviderClient?,
                             syncResult: SyncResult?) {
    val number = phoneNumber(context)

    InviteApi.instance.getInvitesFrom(number)
        .map { it.filter { System.currentTimeMillis() - it.createAt < DAY_IN_MILLIS } }
        .map { it.sortedByDescending { it.createAt } }
        .flatMap { Observable.from(it) }
        .doOnNext {
          if (!userExists(context, it.from.phoneNumber)) insertUser(it.from)
          if (!userExists(context, it.to.phoneNumber)) insertUser(it.to)
        }
        .map { it.toContentValues(context) }
        .map {
          if (inviteExists(context, it.getAsString(InviteEntry.COLUMN_BACKEND_ID))) {
            context.contentResolver
                .update(
                    InviteEntry.CONTENT_URI, it,
                    InviteEntry.COLUMN_BACKEND_ID,
                    arrayOf(it.getAsString(InviteEntry.COLUMN_BACKEND_ID)))
          } else context.contentResolver.insert(InviteEntry.CONTENT_URI, it)
        }
        .count()
        .compose(backoff<Int>(1, TimeUnit.SECONDS, 4))
        .subscribeOn(Schedulers.io())
        .subscribe({
          println("#onPerformSync : insert count = $it")
        }, { e ->
          println("#onPerformSync : error ${e.message}") // error timeout
        })
  }

  private fun phoneNumber(context: Context): String {
    val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return telephony.line1Number
  }

  private fun insertUser(user: User) {
    val values = ContentValues()
    values.put(UserEntry.COLUMN_FIRST_NAME, user.firstName)
    values.put(UserEntry.COLUMN_LAST_NAME, user.lastName)
    values.put(UserEntry.COLUMN_PHONE_NUMBER, user.phoneNumber)
    values.put(UserEntry.COLUMN_PHOTO_URI, user.photoUri)
    context.contentResolver.insert(UserEntry.CONTENT_URI, values)
  }

  companion object {
    const val DAY_IN_MILLIS = 1000 * 60 * 60 * 24
    // Interval at which to sync with the backend, in seconds.
    // 60 seconds (1 minute) * 30 = 3 hours
    val SYNC_INTERVAL = 60 * 180
    val SYNC_FLEXTIME = SYNC_INTERVAL / 3

    /**
     * Helper method to have the sync adapter sync immediately
     */
    fun syncImmediately(context: Context) {
      val bundle = Bundle()
      bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
      bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
      ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle)
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     */
    fun getSyncAccount(context: Context): Account? {
      // Get an instance of the Android account manager
      val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager

      // Create the account type and default account
      val newAccount = Account(
          context.getString(R.string.app_name), context.getString(R.string.sync_account_type))

      // If the password doesn't exist, the account doesn't exist
      if (null == accountManager.getPassword(newAccount)) {
        if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
          return null
        }

        onAccountCreated(newAccount, context)
      }
      return newAccount
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    fun configurePeriodicSync(context: Context, syncInterval: Int, flexTime: Int) {
      val account = getSyncAccount(context)
      val authority = context.getString(R.string.content_authority)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        // we can enable inexact timers in our periodic sync
        val request = SyncRequest.Builder().syncPeriodic(syncInterval.toLong(), flexTime.toLong()).setSyncAdapter(account, authority).setExtras(Bundle()).build()
        ContentResolver.requestSync(request)
      } else {
        ContentResolver.addPeriodicSync(account,
            authority, Bundle(), syncInterval.toLong())
      }
    }

    private fun onAccountCreated(newAccount: Account, context: Context) {
      /*
         * Since we've created an account
         */
      InviteSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME)

      /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
      ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true)

      /*
         * Finally, let's do a sync to get things started
         */
      syncImmediately(context)
    }

    fun initializeSyncAdapter(context: Context) {
      getSyncAccount(context)
    }
  }
}