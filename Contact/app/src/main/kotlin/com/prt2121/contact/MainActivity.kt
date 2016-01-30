package com.prt2121.contact

import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import butterknife.bindView
import com.prt2121.contact.PermissionResult.*
import com.prt2121.contact.Utils.findContact
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by pt2121 on 1/28/16.
 */
class MainActivity : AppCompatActivity() {

  val button: Button by bindView(R.id.button)
  val textView: TextView by bindView(R.id.textView)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_main)
    button.setOnClickListener {
      val i = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
      startActivityForResult(i, CONTACT_PICKER)
    }
    requestPermissionEvents(READ_CONTACTS)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          println(it)
          when (it.result) {
            GRANTED -> {
              Snackbar.make(rootView, "Yeah...", Snackbar.LENGTH_LONG).show()
              button.isEnabled = true
              button.isClickable = true
            }
            SHOW_RATIONALE -> {
              Snackbar.make(rootView, "Please", Snackbar.LENGTH_INDEFINITE).setAction("OK", { requestPermission(READ_CONTACTS) }).show()
              button.isEnabled = false
              button.isClickable = false
            }
            NEVER_ASK_AGAIN -> {
              Snackbar.make(rootView, "Noooo...", Snackbar.LENGTH_LONG).show()
              button.isEnabled = false
              button.isClickable = false
            }
          }
        }
  }

  // https://www.sinch.com/tutorials/android-contact-picker-tutorial/
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      CONTACT_PICKER -> {
        findContact(this, contentResolver, resultCode, data)
            .forEach { textView.text = it.toString() }
      }
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    if (!Perm.onResult(this, requestCode, permissions, grantResults)) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }

  companion object {
    private const val CONTACT_PICKER = 1001
  }
}