package com.prt2121.everywhere.meetup

import android.location.Location
import com.prt2121.everywhere.Rx
import com.prt2121.everywhere.TokenStorage
import com.prt2121.everywhere.meetup.model.Group
import com.prt2121.summon.location.UserLocation
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.RxJavaCallAdapterFactory
import rx.Observable
import java.util.*

/**
 * Created by pt2121 on 1/23/16.
 */
object MeetupUtils {

  fun groupsByLatLng(token: Observable<String>, location: Observable<Location>, radius: Int = 10): Observable<ArrayList<Group>> {
    val service = MeetupUtils.meetupService()
    return location
        .doOnNext { println("it == null ${it == null}") }
        .filter { it != null }
        .flatMap {
          loc ->
          token.flatMap {
            service.groupsByLatLong("Bearer $it", loc.latitude, loc.longitude, radius, "25")
          }
        }
        .compose(Rx.applySchedulers<ArrayList<Group>>())
  }

  fun groups(token: Observable<String>, lat: Double, lng: Double, radius: Int = 10): Observable<ArrayList<Group>> {
    val service = MeetupUtils.meetupService()
    return token.flatMap { service.groupsByLatLong("Bearer $it", lat, lng, radius, "25") }
        .compose(Rx.applySchedulers<ArrayList<Group>>())
  }

  fun groups(token: Observable<String>): Observable<ArrayList<Group>> {
    val service = MeetupUtils.meetupService()
    return token.flatMap { service.groupsByZip("Bearer $it", "10003", "1", "25") }
        .compose(Rx.applySchedulers<ArrayList<Group>>())
  }

  fun meetupService(): MeetupService {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
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
    return service
  }

}