package com.prt2121.capstone

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.invite.Invite
import com.invite.InviteApi
import com.prt2121.sectionlist.SectionRecyclerViewAdapter
import com.prt2121.sectionlist.SectionRecyclerViewAdapter.Section
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by pt2121 on 2/6/16.
 */
class ListActivity : AppCompatActivity(), InviteAdapter.ClickListener {
  override fun onItemViewClick(view: View, invite: Invite) {
    println("invite._id clicked ${invite._id}")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_list)
    val listView = findViewById(R.id.recycler_view) as RecyclerView
    listView.layoutManager = LinearLayoutManager(this)
    val baseAdapter = InviteAdapter(this, arrayListOf(), this)
    val sections = emptyArray<SectionRecyclerViewAdapter.Section>()
    val sectionAdapter = SectionRecyclerViewAdapter(this, R.layout.section, R.id.section_text, baseAdapter)
    sectionAdapter.update(sections)
    listView.adapter = sectionAdapter

    InviteApi.instance.getInvitesFrom("6466445321")
        .map { it.sortedByDescending { it.createAt } }
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
              baseAdapter.update(it)
              val active = it.filter { it.isActive() }.size
              sectionAdapter.update(
                  arrayOf(Section(0, "Active"), Section(active, "Archive"))
              )
            },
            { println("${it.message}") },
            { println("completed") }
        )
  }

}