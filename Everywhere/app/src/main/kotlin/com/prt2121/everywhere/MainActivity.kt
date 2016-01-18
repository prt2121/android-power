package com.prt2121.everywhere

import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import net.smartam.leeloo.client.OAuthClient
import net.smartam.leeloo.client.URLConnectionClient
import net.smartam.leeloo.client.request.OAuthClientRequest
import net.smartam.leeloo.common.exception.OAuthSystemException
import net.smartam.leeloo.common.message.types.GrantType
import org.jetbrains.anko.async

/**
 * Created by pt2121 on 1/17/16.
 */
class MainActivity : BaseActivity() {

  val AUTH_URL = "https://secure.meetup.com/oauth2/authorize"
  val TOKEN_URL = "https://secure.meetup.com/oauth2/access"
  val CONSUMER_KEY = ""
  val CONSUMER_SECRET = ""
  val REDIRECT_URI = "http://prt2121.github.io"

  override fun onCreate(savedInstanceState: Bundle?) {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    super.onCreate(savedInstanceState)
    val webView = WebView(this)
    webView.setWebViewClient(LoginWebViewClient())
    setContentView(webView)

    webView.settings.javaScriptEnabled = true
    try {
      val request = OAuthClientRequest
          .authorizationLocation(AUTH_URL)
          .setClientId(CONSUMER_KEY)
          .setRedirectURI(REDIRECT_URI)
          .buildQueryMessage()
      webView.loadUrl("${request.locationUri}&response_type=code&set_mobile=on")

    } catch (e: OAuthSystemException) {
      println("OAuth request failed ${e.message}")
    }
  }

  inner class LoginWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
      val uri = Uri.parse(url)
      val code = uri.getQueryParameter("code")
      val error = uri.getQueryParameter("error")
      if (code != null) {
        val request = OAuthClientRequest.tokenLocation(TOKEN_URL)
            .setGrantType(GrantType.AUTHORIZATION_CODE)
            .setClientId(CONSUMER_KEY)
            .setClientSecret(CONSUMER_SECRET)
            .setRedirectURI(REDIRECT_URI)
            .setCode(code)
            .buildBodyMessage()
        val oAuthClient = OAuthClient(URLConnectionClient())

        async() {
          val response = oAuthClient.accessToken(request)
          println("accessToken ${response.accessToken}")
          println("expiresIn ${response.expiresIn}")
        }

      } else if (error != null) {
        println("error $error")
      }
      return false
    }
  }
}
