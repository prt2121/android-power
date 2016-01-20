package com.prt2121.everywhere

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import com.prt2121.everywhere.model.Group

/**
 * Created by pt2121 on 1/18/16.
 */
class GroupActivity : BaseActivity(), GroupFragment.OnListFragmentInteractionListener {
  override fun onListFragmentInteraction(group: Group) {
    println("onListFragmentInteraction ${group.id}")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_group)
    val toolbar = findViewById(R.id.toolbar) as Toolbar
    setSupportActionBar(toolbar)

    val fab = findViewById(R.id.fab) as FloatingActionButton
    fab.setOnClickListener { view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show() }

    if (findViewById(R.id.fragment_container) != null && savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
          .add(R.id.fragment_container, GroupFragment.newInstance(1)).commit()
    }
  }
}