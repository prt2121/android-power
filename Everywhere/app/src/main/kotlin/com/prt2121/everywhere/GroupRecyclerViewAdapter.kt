package com.prt2121.everywhere

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.prt2121.everywhere.GroupFragment.OnListFragmentInteractionListener
import com.prt2121.everywhere.model.Group

/**
 * Created by pt2121 on 1/18/16.
 *
 * [RecyclerView.Adapter] that can display a [Group] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class GroupRecyclerViewAdapter(private val groups: MutableList<Group>, private val listener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder>() {

  fun update(gs: List<Group>) {
    groups.clear()
    groups.addAll(gs)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.mItem = groups[position]
    holder.mIdView.text = groups[position].name
    holder.mContentView.text = groups[position].city

    holder.mView.setOnClickListener {
      listener?.onListFragmentInteraction(holder.mItem!!)
    }
  }

  override fun getItemCount(): Int {
    return groups.size
  }

  inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
    val mIdView: TextView
    val mContentView: TextView
    var mItem: Group? = null

    init {
      mIdView = mView.findViewById(R.id.id) as TextView
      mContentView = mView.findViewById(R.id.content) as TextView
    }

    override fun toString(): String {
      return super.toString() + " '" + mContentView.text + "'"
    }
  }
}
