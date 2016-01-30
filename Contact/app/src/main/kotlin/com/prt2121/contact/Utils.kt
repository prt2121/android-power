package com.prt2121.contact

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import kotlinslang.control.None
import kotlinslang.control.Option
import kotlinslang.control.toOption

/**
 * Created by pt2121 on 1/29/16.
 */
object Utils {
  fun findContact(context: Context, contentResolver: ContentResolver, resultCode: Int, data: Intent?): Option<Contact> {
    return if (resultCode == AppCompatActivity.RESULT_OK) {
      val uri = data?.data
      val cursor = contentResolver.query(uri, null, null, null, null)
      cursor.moveToFirst()
      val contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
      val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
      val photoUrlIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
      val hasPhoneNumberIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
      if (cursor.getInt(hasPhoneNumberIndex) == 1) {
        val id = cursor.getLong(contactIdIndex)
        val u = cursor.getString(photoUrlIndex).toOption().map { Uri.parse(it) }
        Contact(
            id,
            cursor.getString(nameIndex),
            u,
            findPhoneNumber(context, id)).toOption()
      } else None
    } else None
  }

  private fun findPhoneNumber(context: Context, contactId: Long): Option<String> {
    val phones = context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
        null, null)
    val phoneNumber = if (phones.moveToNext()) {
      phones.getString(
          phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
      ).toOption()
    } else None
    phones.close()
    return phoneNumber
  }
}