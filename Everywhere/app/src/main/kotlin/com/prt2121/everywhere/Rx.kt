package com.prt2121.everywhere

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by pt2121 on 1/23/16.
 */
object Rx {

  fun <T> applySchedulers(): Observable.Transformer<T, T> =
      Observable.Transformer<T, T> { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }

}