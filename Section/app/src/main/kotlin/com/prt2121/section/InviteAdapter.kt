package com.prt2121.section

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

/**
 * Created by pt2121 on 2/6/16.
 */
class InviteAdapter(val context: Context, val invites: List<Invite>, val listener: ClickListener) : RecyclerView.Adapter<InviteAdapter.ViewHolder>() {
  interface ClickListener {
    fun onItemViewClick(view: View, invite: Invite)
  }

  override fun getItemCount(): Int = invites.size

  override fun onBindViewHolder(p0: ViewHolder?, p1: Int) {
    p0?.bind(invites[p1])
  }

  override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): ViewHolder? {
    val itemView = LayoutInflater.from(context).inflate(R.layout.list_item, p0, false)
    return ViewHolder(itemView, listener)
  }

  class ViewHolder(itemView: View, listener: ClickListener) : RecyclerView.ViewHolder(itemView) {

    private val profileImageView = itemView.findViewById(R.id.profile_image_view) as ImageView
    private val titleTextView = itemView.findViewById(R.id.invite_title) as TextView
    private val dateTextView = itemView.findViewById(R.id.invite_date) as TextView
    private var invite: Invite? = null

    init {
      itemView.setOnClickListener { v ->
        if (invite != null) {
          listener.onItemViewClick(v, invite!!)
        }
      }
    }

    fun bind(invite: Invite) {
      this.invite = invite
      titleTextView.text = invite.title
      dateTextView.text = invite.date
      Picasso.with(itemView.context)
          .load(invite.profilePic)
          .placeholder(R.drawable.profile_placeholder)
          .error(R.drawable.profile_placeholder)
          .transform(CircleTransform())
          .into(profileImageView)
    }
  }
}