package com.prt2121.capstone.data

import android.provider.BaseColumns
import android.test.AndroidTestCase
import com.prt2121.capstone.data.InviteDbHelper.Companion.DATABASE_NAME
import junit.framework.Assert
import java.util.*

/**
 * Created by pt2121 on 2/9/16.
 */
class InviteDbHelperTest : AndroidTestCase() {
  internal fun deleteTheDatabase() {
    mContext.deleteDatabase(DATABASE_NAME)
  }

  public override fun setUp() {
    deleteTheDatabase()
  }

  @Throws(Throwable::class)
  fun testCreateDb() {
    // build a HashSet of all of the table names we wish to look for
    // Note that there will be another table in the DB that stores the
    // Android metadata (db version information)
    val tableNameHashSet = HashSet<String>()
    tableNameHashSet.add(UserEntry.TABLE_NAME)
    tableNameHashSet.add(InviteEntry.TABLE_NAME)


    mContext.deleteDatabase(DATABASE_NAME)
    val db = InviteDbHelper(mContext).writableDatabase
    Assert.assertEquals(true, db.isOpen)

    // have we created the tables we want?
    var c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)

    Assert.assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst())

    // verify that the tables have been created
    do {
      tableNameHashSet.remove(c.getString(0))
    } while (c.moveToNext())

    Assert.assertTrue("Error: database was not created with all tables", tableNameHashSet.isEmpty())

    // now, do our tables contain the correct columns?
    c = db.rawQuery("PRAGMA table_info(" + UserEntry.TABLE_NAME + ")", null)

    Assert.assertTrue("Error: unable to query the database for table information.", c.moveToFirst())

    // Build a HashSet of all of the column names we want to look for
    val userColumnHashSet = HashSet<String>()
    userColumnHashSet.add(BaseColumns._ID)
    userColumnHashSet.add(UserEntry.COLUMN_FIRST_NAME)
    userColumnHashSet.add(UserEntry.COLUMN_LAST_NAME)
    userColumnHashSet.add(UserEntry.COLUMN_PHONE_NUMBER)
    userColumnHashSet.add(UserEntry.COLUMN_PHOTO_URI)

    val columnNameIndex = c.getColumnIndex("name")
    do {
      val columnName = c.getString(columnNameIndex)
      userColumnHashSet.remove(columnName)
    } while (c.moveToNext())

    // if this fails, it means that your database doesn't contain all of the required location
    // entry columns
    Assert.assertTrue("Error: The database doesn't contain all of the required user entry columns",
        userColumnHashSet.isEmpty())
    db.close()
  }

}