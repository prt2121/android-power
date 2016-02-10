package com.invite

import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import retrofit.http.*
import rx.Observable

/**
 * Created by pt2121 on 12/22/15.
 */
class InviteApi {

  private fun api(): Api {
    val retrofit = Retrofit.Builder()
        .baseUrl(INVITE_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build()
    return retrofit.create(Api::class.java)
  }

  fun invite(invite: Invite): Observable<Invite> {
    return api().createInvite(invite)
  }

  fun query(id: String): Observable<Invite> {
    return api().getInvite(id)
  }

  fun getInvitesFrom(phoneNumber: String): Observable<List<Invite>> {
    return api().getInvitesFromPhoneNumber(phoneNumber)
  }

  interface Api {
    @POST("/invites")
    fun createInvite(@Body invite: Invite): Observable<Invite>

    @GET("/invites/{id}")
    fun getInvite(@Path("id") id: String): Observable<Invite>

    @GET("/invites")
    fun getInvitesFromPhoneNumber(
        @Query("fromPhoneNumber") fromPhoneNumber: String): Observable<List<Invite>>
  }

  companion object {
    val INVITE_BASE_URL: String = "https://intense-waters-9652.herokuapp.com/"
    //val INVITE_BASE_URL: String = "http://localhost:8080/"
    val instance = InviteApi()
  }
}