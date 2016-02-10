package com.invite

import com.google.common.truth.Truth.assertThat
import org.junit.Test as test

/**
 * Created by pt2121 on 12/22/15.
 *
 * Don't use! this class tests against real data!
 */
class InviteApiTest {

  @test fun testPost() {
    InviteApi.instance.invite(
        Invite(
            from = User("John", "Smiths", "9071237657"),
            to = User("Jane", "Smiths", "8707234511"),
            destinationAddress = "Quincy market Boston",
            destinationLatLng = "42.3601132,-71.0569562",
            message = "Join me",
            createAt = System.currentTimeMillis()
        )
    ).toBlocking()
        .subscribe({
          println(it)
          assertThat("Join me").isEqualTo(it.message)
        }, {
          println(it.message)
        })
  }

  @test fun testGet() {
    InviteApi.instance.query("56b800c9d4c6de1d2a3ed4e2")
        .toBlocking()
        .subscribe({
          println(it)
          assertThat("Join us!").isEqualTo(it.message)
        }, {
          println(it.message)
        })
  }

  @test fun testGetInvitesFrom() {
    InviteApi.instance.getInvitesFrom("6466445321")
        .toBlocking()
        .subscribe({
          println(it)
          assertThat(it.size).isEqualTo(2)
        }, {
          println(it.message)
        })
  }

}