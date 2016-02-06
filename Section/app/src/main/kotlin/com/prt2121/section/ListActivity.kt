package com.prt2121.section

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.prt2121.sectionlist.SectionRecyclerViewAdapter

/**
 * Created by pt2121 on 2/6/16.
 */
class ListActivity : AppCompatActivity(), InviteAdapter.ClickListener {
  override fun onItemViewClick(view: View, invite: Invite) {
    println("${invite.title}")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_list)
    val listView = findViewById(R.id.recycler_view) as RecyclerView
    listView.layoutManager = LinearLayoutManager(this)
    val baseAdapter = InviteAdapter(this, mockInvites(), this)
    val sections = arrayOf(SectionRecyclerViewAdapter.Section(0, "Section 1"), SectionRecyclerViewAdapter.Section(4, "Section 2"))
    val sectionAdapter = SectionRecyclerViewAdapter(this, R.layout.section, R.id.section_text, baseAdapter)
    println("sections size ${sections.size}")
    sectionAdapter.setSections(sections)
    listView.adapter = sectionAdapter
  }

  fun mockInvites(): List<Invite> {
    val invite1 = Invite(Uri.EMPTY, "title 1", "date 1")
    val invite2 = Invite(Uri.EMPTY, "title 2", "date 2")
    val invite3 = Invite(Uri.EMPTY, "title 3", "date 3")
    val invite4 = Invite(Uri.EMPTY, "title 4", "date 4")
    val invite5 = Invite(Uri.EMPTY, "title 5", "date 5")
    val invite6 = Invite(Uri.EMPTY, "title 6", "date 6")
    val invite7 = Invite(Uri.EMPTY, "title 7", "date 7")
    val invite8 = Invite(Uri.EMPTY, "title 8", "date 8")
    val invite9 = Invite(Uri.EMPTY, "title 9", "date 9")
    return listOf(invite1, invite2, invite3, invite4, invite5, invite6, invite7, invite8, invite9)
  }
}