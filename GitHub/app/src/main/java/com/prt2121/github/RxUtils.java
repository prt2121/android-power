package com.prt2121.github;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by pt2121 on 9/27/15.
 */
public class RxUtils {
  public static <T1> Observable.Transformer<T1, T1> applySchedulers() {
    return observable -> observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
