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
import com.prt2121.everywhere.model.Group
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.RxJavaCallAdapterFactory
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by pt2121 on 1/18/16.
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
class GroupFragment : Fragment() {
  private var mListener: OnListFragmentInteractionListener? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater!!.inflate(R.layout.fragment_group_list, container, false)

    if (view is RecyclerView) {
      view.layoutManager = StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL)
      view.adapter = GroupRecyclerViewAdapter(arrayListOf(), mListener)

      val token = Observable.just(TokenStorage(activity).retrieve())
      val logging = HttpLoggingInterceptor()
      logging.setLevel(Level.BODY)
      val client = OkHttpClient.Builder()
          .addInterceptor(logging)
          .build()

      val retrofit = Retrofit.Builder()
          .baseUrl("https://api.meetup.com")
          .client(client)
          .addConverterFactory(GsonConverterFactory.create())
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .build()
      val service = retrofit.create(MeetupService::class.java)
      token.flatMap { service.groups("Bearer $it", "10003", "1", "25") }
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            println("${it.count()}")
            (view.adapter as GroupRecyclerViewAdapter).update(it)
          }, {
            println(it.message)
          }, {
            println("completed!")
          })
    }
    return view
  }

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    if (context is OnListFragmentInteractionListener) {
      mListener = context as OnListFragmentInteractionListener?
    } else {
      throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
    }
  }

  override fun onDetach() {
    super.onDetach()
    mListener = null
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   *
   *
   * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
   */
  interface OnListFragmentInteractionListener {
    fun onListFragmentInteraction(group: Group)
  }

  companion object {

    @SuppressWarnings("unused") fun newInstance(): GroupFragment {
      return GroupFragment()
    }
  }
}