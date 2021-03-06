package com.prt2121.everywhere

import rx.Observable

/**
 * Created by pt2121 on 1/18/16.
 */
interface ITokenStorage {
  fun save(token: String)
  fun retrieve(): Observable<String>
}