package com.prt2121.capstone

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import butterknife.bindView
import com.invite.Invite
import com.invite.InviteParcel
import com.prt2121.capstone.data.InviteEntry
import com.prt2121.capstone.data.InviteProvider
import com.prt2121.capstone.sync.InviteSyncAdapter
import com.prt2121.sectionlist.SectionRecyclerViewAdapter

/**
 * An activity representing a list of Invites. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link InviteDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class InviteListActivity : AppCompatActivity(), InviteAdapter.ClickListener, LoaderManager.LoaderCallbacks<Cursor> {
  private val listView: RecyclerView  by bindView(R.id.invite_list)
  private val toolbar: Toolbar  by bindView(R.id.toolbar)
  private val baseAdapter by lazy { InviteAdapter(this, arrayListOf(), this) }
  private val sectionAdapter by lazy { SectionRecyclerViewAdapter(this, R.layout.section, R.id.section_text, baseAdapter) }

  override fun onItemViewClick(view: View, invite: Invite) {
    val uri = InviteEntry.buildUri(invite.id!!.toLong())
    if (twoPane) {
      val arguments = Bundle()
      arguments.putParcelable(InviteDetailFragment.ARG_INVITE_URI, uri)
      val fragment = InviteDetailFragment()
      fragment.arguments = arguments
      supportFragmentManager.beginTransaction().replace(R.id.invite_detail_container, fragment).commit()
    } else {
      println("invite uri ${uri.toString()} ${invite.from.phoneNumber}")
      val intent = Intent(this, InviteDetailActivity::class.java)
      intent.putExtra(InviteDetailFragment.ARG_INVITE_URI, uri)
      intent.putExtra(InviteDetailFragment.ARG_INVITE, InviteParcel.wrap(invite))
      startActivity(intent)
    }
  }

  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet
   * device.
   */
  private var twoPane: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_invite_list)

    setSupportActionBar(toolbar)
    toolbar.title = title

    setupRecyclerView(listView)

    if (findViewById(R.id.invite_detail_container) != null) {
      twoPane = true
    }
    supportLoaderManager.initLoader<Cursor>(0, null, this)

    InviteSyncAdapter.initializeSyncAdapter(this)
  }

  private fun setupRecyclerView(listView: RecyclerView) {
    listView.layoutManager = LinearLayoutManager(this)
    val sections = emptyArray<SectionRecyclerViewAdapter.Section>()
    sectionAdapter.update(sections)
    listView.adapter = sectionAdapter
  }

  override fun onLoadFinished(loader: Loader<Cursor>?, cursor: Cursor?) {
    if (cursor != null && cursor.moveToFirst()) {
      val ls = arrayListOf<Invite>()
      do {
        val invite = InviteProvider.inviteFromCursor(cursor)
        ls.add(invite)
      } while (cursor.moveToNext())

      baseAdapter.update(ls)
      val lastIndexOfActive = ls.filter { it.isActive() }.size
      val activeLabel = if (lastIndexOfActive == 0) "No Active Invite" else "Active"
      val archiveLabel = if (ls.isEmpty()) "No Archive Invite" else "Archive"
      sectionAdapter.update(
          arrayOf(SectionRecyclerViewAdapter.Section(0, activeLabel),
              SectionRecyclerViewAdapter.Section(lastIndexOfActive, archiveLabel))
      )
    }
  }

  override fun onLoaderReset(p0: Loader<Cursor>?) {
    sectionAdapter.update(emptyArray<SectionRecyclerViewAdapter.Section>())
  }

  override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor>? =
      CursorLoader(this,
          InviteEntry.QUERY_URI,
          null,
          null,
          null,
          null)
}