package com.prt2121.everywhere.meetup

import com.prt2121.everywhere.meetup.model.Group
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import rx.Observable
import java.util.*

/**
 * Created by pt2121 on 1/17/16.
 */
interface MeetupService {
  @GET("find/groups")
  fun groups(
      @Header("Authorization") token: String,
      @Query("zip") zip: String,
      @Query("radius") radius: String,
      @Query("category") category: String): Observable<ArrayList<Group>>
}