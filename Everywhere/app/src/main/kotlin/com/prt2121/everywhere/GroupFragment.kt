package com.prt2121.everywhere

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.prt2121.everywhere.meetup.MeetupUtils
import com.prt2121.everywhere.meetup.model.Group
import com.prt2121.summon.location.UserLocation
import rx.Subscription
import rx.functions.Action0
import rx.functions.Action1

/**
 * Created by pt2121 on 1/18/16.
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener] interface.
 */
class GroupFragment : Fragment() {
  private var listener: OnListFragmentInteractionListener? = null
  private var subscription: Subscription? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater!!.inflate(R.layout.fragment_group_list, container, false)

    if (view is RecyclerView) {
      view.layoutManager = StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL)
      view.adapter = GroupRecyclerViewAdapter(arrayListOf(), listener)
      subscription = MeetupUtils.groupsByLatLng(TokenStorage(activity).retrieve(), UserLocation(activity).lastBestLocation())
          .subscribe(
              { (view.adapter as GroupRecyclerViewAdapter).call(it) }
              , { println(it.message) }
              , { println("completed") }
          )
    }
    return view
  }

  override fun onDestroyView() {
    super.onDestroyView()
    if (subscription != null && !subscription!!.isUnsubscribed) {
      subscription?.unsubscribe()
    }
  }

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    if (context is OnListFragmentInteractionListener) {
      listener = context as OnListFragmentInteractionListener?
    } else {
      throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
    }
  }

  override fun onDetach() {
    super.onDetach()
    listener = null
  }

  interface OnListFragmentInteractionListener {
    fun onListFragmentInteraction(group: Group)
  }

  companion object {
    fun newInstance(): GroupFragment {
      return GroupFragment()
    }
  }
}