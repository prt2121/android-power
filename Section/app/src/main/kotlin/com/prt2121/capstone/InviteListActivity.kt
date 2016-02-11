package com.prt2121.capstone

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.BaseColumns
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
import com.invite.Status
import com.invite.User
import com.prt2121.capstone.data.InviteDbHelper
import com.prt2121.capstone.data.InviteEntry
import com.prt2121.capstone.data.UserEntry
import com.prt2121.sectionlist.SectionRecyclerViewAdapter
import junit.framework.Assert

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
    println("invite._id clicked ${invite._id}")
    if (mTwoPane) {
      //      val arguments = Bundle()
      //      arguments.putString(InviteDetailFragment.ARG_INVITE_URI, holder.mItem.id)
      val fragment = InviteDetailFragment()
      //      fragment.arguments = arguments
      supportFragmentManager.beginTransaction().replace(R.id.invite_detail_container, fragment).commit()
    } else {
      val intent = Intent(this, InviteDetailActivity::class.java)
      //intent.putExtra(InviteDetailFragment.ARG_INVITE_URI, holder.mItem.id)
      startActivity(intent)
    }
  }

  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet
   * device.
   */
  private var mTwoPane: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    test()

    setContentView(R.layout.activity_invite_list)

    setSupportActionBar(toolbar)
    toolbar.title = title

    setupRecyclerView(listView)

    if (findViewById(R.id.invite_detail_container) != null) {
      mTwoPane = true
    }
    supportLoaderManager.initLoader<Cursor>(0, null, this)
  }

  private fun setupRecyclerView(listView: RecyclerView) {
    listView.layoutManager = LinearLayoutManager(this)
    val sections = emptyArray<SectionRecyclerViewAdapter.Section>()
    sectionAdapter.update(sections)
    listView.adapter = sectionAdapter

    // TODO: remove
//    InviteApi.instance.getInvitesFrom("6466445321")
//        .map { it.sortedByDescending { it.createAt } }
//        .subscribeOn(Schedulers.newThread())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(
//            {
//              baseAdapter.update(it)
//              val active = it.filter { it.isActive() }.size
//              sectionAdapter.update(
//                  arrayOf(SectionRecyclerViewAdapter.Section(0, "Active"), SectionRecyclerViewAdapter.Section(active, "Archive"))
//              )
//            },
//            { println("${it.message}") },
//            { println("completed") }
//        )
  }

  override fun onLoadFinished(loader: Loader<Cursor>?, cursor: Cursor?) {
    if (cursor != null && cursor.moveToFirst()) {

      val statusStr = cursor.getString(cursor.getColumnIndex(InviteEntry.COLUMN_STATUS))
      val status = when (statusStr) {
        "ACCEPT" -> Status.ACCEPT
        "PENDING" -> Status.PENDING
        "REJECT" -> Status.REJECT
        else -> Status.CANCEL
      }

      val ls = arrayListOf<Invite>()
      do {
        val invite = Invite(
            cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)).toString(),
            User(cursor.getString(16), cursor.getString(17), cursor.getString(18), cursor.getString(19)),
            User(cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14)),
            cursor.getString(cursor.getColumnIndex(InviteEntry.COLUMN_DESTINATION_LATLNG)),
            cursor.getString(cursor.getColumnIndex(InviteEntry.COLUMN_DESTINATION_ADDRESS)),
            cursor.getString(cursor.getColumnIndex(InviteEntry.COLUMN_MESSAGE)),
            status,
            cursor.getString(cursor.getColumnIndex(InviteEntry.COLUMN_PICKUP_ADDRESS)),
            cursor.getLong(cursor.getColumnIndex(InviteEntry.COLUMN_CREATE_AT))
        )
        ls.add(invite)
      } while (cursor.moveToNext())

      baseAdapter.update(ls)
      val active = ls.filter { it.isActive() }.size
      sectionAdapter.update(
          arrayOf(SectionRecyclerViewAdapter.Section(0, "Active"), SectionRecyclerViewAdapter.Section(active, "Archive"))
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

  fun test() {
    // insert our test records into the database
    val dbHelper = InviteDbHelper(this)
    val db = dbHelper.writableDatabase

    val john = createJohn()
    val jane = createJane()

    val johnId = insertUserValues(this, john)
    val janeId = insertUserValues(this, jane)

    val values = createInviteValues(johnId, janeId)

    val rowId = db.insert(InviteEntry.TABLE_NAME, null, values)
    db.close()
  }

  fun createInviteValues(from: Long, to: Long): ContentValues {
    // Create a new map of values, where column names are the keys
    val testValues = ContentValues()
    testValues.put(InviteEntry.COLUMN_BACKEND_ID, 1234567)
    testValues.put(InviteEntry.COLUMN_CREATE_AT, System.currentTimeMillis())
    testValues.put(InviteEntry.COLUMN_DESTINATION_ADDRESS, "des address")
    testValues.put(InviteEntry.COLUMN_DESTINATION_LATLNG, "des lat long")
    testValues.put(InviteEntry.COLUMN_FROM_ID, from)
    testValues.put(InviteEntry.COLUMN_TO_ID, to)
    testValues.put(InviteEntry.COLUMN_MESSAGE, "test message")
    testValues.put(InviteEntry.COLUMN_PICKUP_ADDRESS, "pickup address")
    testValues.put(InviteEntry.COLUMN_STATUS, "PENDING")
    return testValues
  }

  fun insertUserValues(context: Context, values: ContentValues): Long {
    // insert our test records into the database
    val dbHelper = InviteDbHelper(context)
    val db = dbHelper.writableDatabase

    val id = db.insert(UserEntry.TABLE_NAME, null, values)

    // Verify we got a row back.
    Assert.assertTrue("Error: Failure to insert User Values", id > -1)

    return id
  }

  fun createJohn(): ContentValues {
    // Create a new map of values, where column names are the keys
    val testValues = ContentValues()
    testValues.put(UserEntry.COLUMN_FIRST_NAME, "John")
    testValues.put(UserEntry.COLUMN_LAST_NAME, "Doe")
    testValues.put(UserEntry.COLUMN_PHONE_NUMBER, "9086447097")
    testValues.put(UserEntry.COLUMN_PHOTO_URI, "uri 1")
    return testValues
  }

  fun createJane(): ContentValues {
    // Create a new map of values, where column names are the keys
    val testValues = ContentValues()
    testValues.put(UserEntry.COLUMN_FIRST_NAME, "Jane")
    testValues.put(UserEntry.COLUMN_LAST_NAME, "Frank")
    testValues.put(UserEntry.COLUMN_PHONE_NUMBER, "6464760895")
    testValues.put(UserEntry.COLUMN_PHOTO_URI, "uri 2")
    return testValues
  }
}

//val _id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
//val id = cursor.getInt(cursor.getColumnIndex(InviteEntry.COLUMN_BACKEND_ID))
//val fromId = cursor.getInt(cursor.getColumnIndex(InviteEntry.COLUMN_FROM_ID))
//val toId = cursor.getInt(cursor.getColumnIndex(InviteEntry.COLUMN_TO_ID))
//val destinationLatLng = cursor.getString(cursor.getColumnIndex(InviteEntry.COLUMN_DESTINATION_LATLNG))
//val destinationAddress = cursor.getString(cursor.getColumnIndex(InviteEntry.COLUMN_DESTINATION_ADDRESS))
//val message = cursor.getString(cursor.getColumnIndex(InviteEntry.COLUMN_MESSAGE))
//val status = cursor.getString(cursor.getColumnIndex(InviteEntry.COLUMN_STATUS))
//val pickupAddress = cursor.getString(cursor.getColumnIndex(InviteEntry.COLUMN_PICKUP_ADDRESS))
//val createAt = cursor.getLong(cursor.getColumnIndex(InviteEntry.COLUMN_CREATE_AT))
//val idTo = cursor.getInt(10)
//val firstNameTo = cursor.getString(11)
//val lastNameTo = cursor.getString(12)
//val phoneNumberTo = cursor.getString(13)
//val photoUriTo = cursor.getString(14)
//val idFrom = cursor.getInt(15)
//val firstNameFrom = cursor.getString(16)
//val lastNameFrom = cursor.getString(17)
//val phoneNumberFrom = cursor.getString(18)
//val photoUriFrom = cursor.getString(19)
//
//val str = """
//        _id $_id
//        id $id
//        fromId $fromId
//        toId $toId
//        destinationLatLng $destinationLatLng
//        destinationAddress $destinationAddress
//        message $message
//        status $status
//        pickupAddress $pickupAddress
//        createAt $createAt
//        idTo $idTo
//        firstNameTo $firstNameTo
//        lastNameTo $lastNameTo
//        phoneNumberTo $phoneNumberTo
//        photoUriTo $photoUriTo
//        idFrom $idFrom
//        firstNameFrom $firstNameFrom
//        lastNameFrom $lastNameFrom
//        phoneNumberFrom $phoneNumberFrom
//        photoUriFrom $photoUriFrom
//        """
//
//println(str)