package com.prt2121.capstone

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.bindView
import com.prt2121.capstone.data.InviteProvider
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.toOption

/**
 * A fragment representing a single Invite detail screen.
 * This fragment is either contained in a [InviteListActivity]
 * in two-pane mode (on tablets) or a [InviteDetailActivity]
 * on handsets.
 */
class InviteDetailFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
  private val detailTextView: TextView  by bindView(R.id.invite_detail)
  private val uri: Option<Uri> by lazy {
    if (arguments.containsKey(ARG_INVITE_URI)) arguments.getParcelable<Uri>(InviteDetailFragment.ARG_INVITE_URI).toOption()
    else None
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    loaderManager.initLoader(DETAIL_LOADER, null, this)
    super.onActivityCreated(savedInstanceState)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater!!.inflate(R.layout.invite_detail, container, false)
  }

  override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? =
      uri.map {
        CursorLoader(activity, it, null, null, null, null)
      }.orNull()

  override fun onLoaderReset(p0: Loader<Cursor>?) {
  }

  override fun onLoadFinished(p0: Loader<Cursor>?, data: Cursor?) {
    if (data != null && data.moveToFirst()) {
      val invite = InviteProvider.inviteFromCursor(data)
      detailTextView.text = invite._id
    }
  }

  companion object {
    const val ARG_INVITE_URI = "invite_uri"
    const val DETAIL_LOADER = 0
  }
}