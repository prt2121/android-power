package com.prt2121.capstone

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.invite.Invite
import com.squareup.picasso.Picasso
import org.funktionale.option.Option
import org.funktionale.option.toOption
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.toSingletonObservable
import rx.schedulers.Schedulers
import kotlin.concurrent.thread

/**
 * Created by pt2121 on 2/6/16.
 */
class InviteAdapter(val context: Context, val invites: MutableList<Invite>, val listener: ClickListener) :
    RecyclerView.Adapter<InviteAdapter.ViewHolder>() {

  fun update(p1: List<Invite>) {
    this.invites.clear()
    this.invites.addAll(p1)
    notifyDataSetChanged()
  }

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
      titleTextView.text = invite.message
      dateTextView.text = invite.readableCreateAt()
      invite.to.photoUri.toOption()
          .toSingletonObservable()
          .flatMap { option: Option<String> ->
            if (option.isEmpty() || option.get().isEmpty()) {
              Utils.getContactPhotoUri(itemView.context, invite.to.phoneNumber)
            } else {
              Observable.just(option)
            }
          }
          .subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            it.forEach {
              Picasso.with(itemView.context)
                  .load(Uri.parse(it))
                  .placeholder(R.drawable.profile_placeholder)
                  .error(R.drawable.profile_placeholder)
                  .transform(CircleTransform())
                  .into(profileImageView)
            }
          }, {
            println(it.message)
          })
    }
  }

}

object Utils {
  fun getContactPhotoUri(context: Context, phoneNumber: String): Observable<Option<String>> = Observable.create<Option<String>> { subscriber ->
    thread() {
      val cr = context.contentResolver
      val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
      val cursor = cr.query(uri, arrayOf(ContactsContract.PhoneLookup.PHOTO_URI), null, null, null) ?: null
      val photoUri = if (cursor != null && cursor.moveToFirst()) {
        cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI))
      } else null
      if (cursor != null && !cursor.isClosed) {
        cursor.close()
      }
      if (!subscriber.isUnsubscribed) {
        if (photoUri != null) subscriber.onNext(Option.Some(photoUri))
        else subscriber.onNext(Option.None)
        subscriber.onCompleted()
      }
    }
  }
}
