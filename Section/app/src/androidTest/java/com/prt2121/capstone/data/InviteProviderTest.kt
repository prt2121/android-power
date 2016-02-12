package com.prt2121.capstone.data

import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.test.AndroidTestCase
import android.util.Log
import junit.framework.Assert

/**
 * Created by pt2121 on 2/9/16.
 */
class InviteProviderTest : AndroidTestCase() {

  @Throws(Exception::class)
  override fun setUp() {
    super.setUp()
    deleteAllRecords()
  }

  fun testProviderRegistry() {
    val pm = mContext.packageManager

    val componentName = ComponentName(mContext.packageName, InviteProvider::class.java.name)
    try {
      // Fetch the provider info using the component name from the PackageManager
      // This throws an exception if the provider isn't registered.
      val providerInfo = pm.getProviderInfo(componentName, 0)

      // Make sure that the registered authority matches the authority from the Contract.
      assertEquals("Error: InviteProvider registered with authority: " + providerInfo.authority +
          " instead of authority: " + InviteContract.CONTENT_AUTHORITY,
          providerInfo.authority, InviteContract.CONTENT_AUTHORITY)
    } catch (e: PackageManager.NameNotFoundException) {
      // I guess the provider isn't registered correctly.
      Assert.assertTrue("Error: InviteProvider not registered at " + mContext.packageName, false)
    }
  }

  fun testGetType() {
    var type: String = mContext.contentResolver.getType(InviteEntry.CONTENT_URI)
    assertEquals("Error: the InviteEntry CONTENT_URI should return InviteEntry.CONTENT_TYPE", InviteEntry.CONTENT_TYPE, type)

    type = mContext.contentResolver.getType(UserEntry.CONTENT_URI)
    assertEquals("Error: the UserEntry CONTENT_URI should return UserEntry.CONTENT_TYPE", UserEntry.CONTENT_TYPE, type)
  }

  fun testBasicQuery() {
    // insert our test records into the database
    val dbHelper = InviteDbHelper(mContext)
    val db = dbHelper.writableDatabase

    val john = createJohn()
    val jane = createJane()

    val johnId = insertUserValues(mContext, john)
    val janeId = insertUserValues(mContext, jane)

    val values = createInviteValues(johnId, janeId)

    val rowId = db.insert(InviteEntry.TABLE_NAME, null, values)
    Assert.assertTrue("Unable to Insert InviteEntry into the Database", rowId > -1)

    db.close()

    // Test the basic content provider query
    val cursor = mContext.contentResolver.query(
        InviteEntry.CONTENT_URI,
        null,
        null,
        null,
        null)

    // Make sure we get the correct cursor out of the database
    validateCursor("testBasicQuery", cursor, values)
  }

  fun testUserExist() {
    val dbHelper = InviteDbHelper(mContext)
    val db = dbHelper.writableDatabase

    val john = createJohn()
    val jane = createJane()

    val johnId = insertUserValues(mContext, john)
    val janeId = insertUserValues(mContext, jane)

    val values = createInviteValues(johnId, janeId)

    val rowId = db.insert(InviteEntry.TABLE_NAME, null, values)
    Assert.assertTrue("Unable to Insert InviteEntry into the Database", rowId > -1)

    db.close()

    val selection = UserEntry.COLUMN_PHONE_NUMBER + " = ? "
    val cursor = mContext.contentResolver.query(UserEntry.CONTENT_URI, null, selection, arrayOf("9086447097"), null)
    Log.d(InviteProviderTest::class.java.simpleName, "cursor count ${cursor.count}")
    Assert.assertEquals(1, cursor.count)
  }

  fun validateCursor(error: String, valueCursor: Cursor, expectedValues: ContentValues) {
    Assert.assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst())
    validateCurrentRecord(error, valueCursor, expectedValues)
    valueCursor.close()
  }

  fun validateCurrentRecord(error: String, valueCursor: Cursor, expectedValues: ContentValues) {
    val valueSet = expectedValues.valueSet()
    valueSet.forEach { entry ->
      val columnName = entry.key
      val idx = valueCursor.getColumnIndex(columnName)
      Assert.assertFalse("Column '$columnName' not found. $error", idx == -1)
      val expectedValue = entry.value.toString()
      Assert.assertEquals("Value '" + entry.value.toString() +
          "' did not match the expected value '" +
          expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx))
    }
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

  fun deleteAllRecords() {
    mContext.contentResolver.delete(
        InviteEntry.CONTENT_URI,
        null,
        null)
    mContext.contentResolver.delete(
        UserEntry.CONTENT_URI,
        null,
        null)

    var cursor: Cursor = mContext.contentResolver.query(
        InviteEntry.CONTENT_URI,
        null,
        null,
        null,
        null)
    Assert.assertEquals("Error: Records not deleted from Invite table during delete", 0, cursor.count)
    cursor.close()

    cursor = mContext.contentResolver.query(
        UserEntry.CONTENT_URI,
        null,
        null,
        null,
        null)
    Assert.assertEquals("Error: Records not deleted from User table during delete", 0, cursor.count)
    cursor.close()
  }

}