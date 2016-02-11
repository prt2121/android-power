package com.prt2121.capstone

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.bindView
import com.prt2121.capstone.data.InviteEntry
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
  private var uri: Option<Uri> = None

  private val appBarLayout: CollapsingToolbarLayout  by bindView(R.id.toolbar_layout)
  private val detailTextView: TextView  by bindView(R.id.invite_detail)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (arguments.containsKey(ARG_INVITE_URI)) {
      uri = arguments.getParcelable<Uri>(InviteDetailFragment.ARG_INVITE_URI).toOption()
    }
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

  override fun onLoaderReset(p0: Loader<Cursor>?) {}

  override fun onLoadFinished(p0: Loader<Cursor>?, data: Cursor?) {
    if (data != null && data.moveToFirst()) {
      appBarLayout.title = data.getString(data.getColumnIndex(InviteEntry.COLUMN_DESTINATION_ADDRESS))
      detailTextView.text = data.getString(data.getColumnIndex(InviteEntry.COLUMN_MESSAGE))
    }
  }

  companion object {
    const val ARG_INVITE_URI = "invite_uri"
    const val DETAIL_LOADER = 0
  }
}