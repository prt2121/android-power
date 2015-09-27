package com.prt2121.github.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.prt2121.github.MainActivity;
import com.prt2121.github.R;
import com.prt2121.githubsdk.model.response.Token;
import com.prt2121.githubsdk.model.response.User;
import com.prt2121.githubsdk.service.auth.AccountsHelper;
import com.prt2121.githubsdk.service.auth.RequestToken;
import com.prt2121.githubsdk.service.user.AuthUser;
import com.squareup.okhttp.HttpUrl;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class WebLoginActivity extends AppCompatActivity {

  public static final String ARG_ACCOUNT_TYPE = "ARG_ACCOUNT_TYPE";

  public static final String ARG_AUTH_TYPE = "ARG_AUTH_TYPE";

  public static final String ADDING_FROM_ACCOUNTS = "ADDING_FROM_ACCOUNTS";

  public static final String OAUTH_HOST = "www.github.com";

  private AccountAuthenticatorResponse authenticatorResponse = null;

  private RequestToken requestToken;

  private AccountManager accountManager;

  private AlertDialog progressDialog;
  private String accountType;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Timber.d("uid " + getApplicationInfo().uid);
    authenticatorResponse =
        getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
    if (authenticatorResponse != null) {
      authenticatorResponse.onRequestContinued();
    }
    accountManager = AccountManager.get(this);
    accountType = getString(R.string.account_type);
    Account[] accounts = accountManager.getAccountsByType(accountType);
    if (accounts != null && accounts.length > 0) {
      openMain();
    }
    WebView webView = prepareWebView();
    setContentView(webView);
  }

  @NonNull private WebView prepareWebView() {
    String initialScope = "user,public_repo,repo,delete_repo,notifications,gist";
    HttpUrl.Builder url = new HttpUrl.Builder().scheme("https")
        .host(OAUTH_HOST)
        .addPathSegment("login")
        .addPathSegment("oauth")
        .addPathSegment("authorize")
        .addQueryParameter("client_id", getResources().getString(R.string.gh_client_id))
        .addQueryParameter("scope", initialScope);

    WebView webView = new WebView(this);
    webView.loadUrl(url.toString());
    webView.setWebViewClient(new WebViewClient() {
      LightProgressDialog dialog =
          (LightProgressDialog) LightProgressDialog.create(WebLoginActivity.this, R.string.loading);

      @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
        //dialog.show();
      }

      @Override public void onPageFinished(WebView view, String url) {
        //dialog.dismiss();
      }

      @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);
        if (uri.getScheme().equals(getString(R.string.github_oauth_scheme))) {
          onUserLoggedIn(uri);
          return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
      }
    });
    return webView;
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Uri uri = intent.getData();
    onUserLoggedIn(uri);
  }

  private void onUserLoggedIn(Uri uri) {
    if (uri != null && uri.getScheme().equals(getString(R.string.github_oauth_scheme))) {
      //openLoadingDialog();
      String code = uri.getQueryParameter("code");
      if (requestToken == null && code != null) {
        requestToken = new RequestToken(WebLoginActivity.this, code);
        requestToken.token().map(this::getTokenOrThrowException)
            //.doOnNext(t -> showLoadingUser())
            .flatMap(this::getUserAndToken)
            .doOnNext(t -> Timber.d(t.first.name))
            .compose(applySchedulers())
            .subscribe(pair -> {
              setupAccount(pair);
              openMain();
            }, error -> {
              Timber.e(error.getMessage());
            });
      }
    }
  }

  private void setupAccount(Pair<User, Token> pair) {
    User user = pair.first;
    Token token = pair.second;
    Account account = new Account(user.login, accountType);
    Bundle userData =
        AccountsHelper.buildBundle(account.name, user.email, user.avatar_url, token.scope);
    final String tokenString = token.access_token;
    userData.putString(AccountManager.KEY_AUTHTOKEN, tokenString);
    accountManager.addAccountExplicitly(account, null, userData);
    accountManager.setAuthToken(account, accountType, tokenString);

    Bundle result = new Bundle();
    result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
    result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
    result.putString(AccountManager.KEY_AUTHTOKEN, tokenString);

    if (authenticatorResponse != null) {
      authenticatorResponse.onResult(result);
      authenticatorResponse = null;
    }
  }

  @NonNull private Token getTokenOrThrowException(Token token) {
    if (token.error != null || TextUtils.isEmpty(token.access_token)) {
      Observable.error(new Exception(token.error));
    }
    return token;
  }

  @NonNull private Observable<? extends Pair<User, Token>> getUserAndToken(Token token) {
    AuthUser authUser = new AuthUser(WebLoginActivity.this, token.access_token);
    return authUser.me()
        .zipWith(Observable.just(token), (Func2<User, Token, Pair<User, Token>>) Pair::new);
  }

  //private void showLoadingUser() {
  //  WebLoginActivity.this.runOnUiThread(
  //      () -> progressDialog.setMessage(getString(R.string.loading_user)));
  //}
  //
  //private void openLoadingDialog() {
  //  progressDialog = LightProgressDialog.create(this, R.string.login_activity_authenticating);
  //  progressDialog.show();
  //}

  protected <T1> Observable.Transformer<T1, T1> applySchedulers() {
    return observable -> observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  private void openMain() {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}
