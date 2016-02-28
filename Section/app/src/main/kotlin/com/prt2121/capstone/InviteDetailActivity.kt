package com.prt2121.capstone

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.invite.InviteParcel

/**
 * An activity representing a single Invite detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [InviteListActivity].
 */
class InviteDetailActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_invite_detail)
    val toolbar = findViewById(R.id.detail_toolbar) as Toolbar
    setSupportActionBar(toolbar)

    val actionBar = supportActionBar
    actionBar?.setDisplayHomeAsUpEnabled(true)

    if (savedInstanceState == null) {
      val arguments = Bundle()
      arguments.putParcelable(InviteDetailFragment.ARG_INVITE_URI, intent.getParcelableExtra(InviteDetailFragment.ARG_INVITE_URI))
      val inviteParcel = intent.getParcelableExtra<InviteParcel>(InviteDetailFragment.ARG_INVITE)
      arguments.putParcelable(InviteDetailFragment.ARG_INVITE, inviteParcel)

      val fragment = InviteDetailFragment()
      fragment.arguments = arguments
      supportFragmentManager.beginTransaction().add(R.id.invite_detail_container, fragment).commit()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId
    if (id == android.R.id.home) {
      // allow users to navigate up one level in the application structure.
      //
      NavUtils.navigateUpTo(this, Intent(this, InviteListActivity::class.java))
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}