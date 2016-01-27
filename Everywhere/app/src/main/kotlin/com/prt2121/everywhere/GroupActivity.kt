package com.prt2121.everywhere

import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.prt2121.everywhere.meetup.model.Group

/**
 * Created by pt2121 on 1/18/16.
 */
class GroupActivity : BaseActivity(), GroupFragment.OnListFragmentInteractionListener {
  override fun onListFragmentInteraction(group: Group) {
    println("group.id ${group.id}")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_group)

    val toolbar = findViewById(R.id.toolbar) as Toolbar
    setSupportActionBar(toolbar)

    if (findViewById(R.id.fragment_container) != null && savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
          .add(R.id.fragment_container, GroupFragment.newInstance()).commit()
    }
  }
}