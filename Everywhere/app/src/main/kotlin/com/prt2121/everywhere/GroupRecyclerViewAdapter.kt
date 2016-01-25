package com.prt2121.everywhere

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.prt2121.everywhere.GroupFragment.OnListFragmentInteractionListener
import com.prt2121.everywhere.meetup.model.Group
import java.util.*

/**
 * Created by pt2121 on 1/18/16.
 *
 * [RecyclerView.Adapter] that can display a [Group] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class GroupRecyclerViewAdapter(private val groups: MutableList<Group>,
                               private val listener: OnListFragmentInteractionListener?) :
    RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder>(), Function1<ArrayList<Group>, Unit> {

  override fun invoke(p1: ArrayList<Group>) {
    this.groups.clear()
    this.groups.addAll(p1)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.item = groups[position]
    holder.idView.text = groups[position].name
    holder.contentView.text = groups[position].city

    holder.view.setOnClickListener {
      listener?.onListFragmentInteraction(holder.item!!)
    }
  }

  override fun getItemCount(): Int {
    return groups.size
  }

  inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val idView: TextView
    val contentView: TextView
    var item: Group? = null

    init {
      idView = view.findViewById(R.id.id) as TextView
      contentView = view.findViewById(R.id.content) as TextView
    }

    override fun toString(): String {
      return super.toString() + " '" + contentView.text + "'"
    }
  }
}
