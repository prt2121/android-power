package com.prt2121.contact

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import butterknife.bindView
import com.prt2121.contact.Perm.appPermissions
import com.prt2121.contact.Utils.findContact
import kotlinslang.orElse

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
    requestPermission(Manifest.permission.READ_CONTACTS) { // show rationale so we can request the permission again!
      println(resources.getString(R.string.permission_contacts_rationale))
    }
  }

  // https://www.sinch.com/tutorials/android-contact-picker-tutorial/
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      CONTACT_PICKER -> {
        val contact = findContact(this, contentResolver, resultCode, data)
        contact.forEach { c ->
          println(
              """
              name ${c.name}
              photoUrl ${c.profilePic.map { it.toString() }.orElse(" no photo ")}
              phoneNumber ${c.phoneNumber.orElse("")}"""
          )
          textView.text = c.toString()
        }
      }
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    when (requestCode) {
      appPermissions.indexOf(Manifest.permission.READ_CONTACTS) -> {
        if (Perm.verifyPermissions(grantResults)) println("GRANTED")
        else println("permission NOT granted")
      }
      else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }

  companion object {
    private const val CONTACT_PICKER = 1001
  }
}