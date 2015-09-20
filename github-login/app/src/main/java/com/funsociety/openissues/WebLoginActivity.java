package com.funsociety.openissues;

import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.sdk.bean.dto.response.Token;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.sdk.services.login.RequestTokenClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.squareup.okhttp.HttpUrl;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class WebLoginActivity extends AppCompatActivity
        implements BaseClient.OnResultCallback<User> {

    public static final String ARG_ACCOUNT_TYPE = "ARG_ACCOUNT_TYPE";

    public static final String ARG_AUTH_TYPE = "ARG_AUTH_TYPE";

    public static final String ADDING_FROM_ACCOUNTS = "ADDING_FROM_ACCOUNTS";

    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;

    public static final String OAUTH_HOST = "www.github.com";

    private RequestTokenClient requestTokenClient;

    private String accessToken;

    private String scope;

    private AccountManager accountManager;

    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }

        accountManager = AccountManager.get(this);

        String initialScope = "user,public_repo,repo,delete_repo,notifications,gist";
        // https://github.com/login/oauth/authorize
        HttpUrl.Builder url = new HttpUrl.Builder()
                .scheme("https")
                .host(OAUTH_HOST)
                .addPathSegment("login")
                .addPathSegment("oauth")
                .addPathSegment("authorize")
                .addQueryParameter("client_id", getResources().getString(R.string.gh_client_id))
                .addQueryParameter("scope", initialScope);

        WebView webView = new WebView(this);
        webView.loadUrl(url.toString());
        webView.setWebViewClient(new WebViewClient() {
            LightProgressDialog dialog = (LightProgressDialog) LightProgressDialog.create(
                    WebLoginActivity.this, R.string.loading);

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                dialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dialog.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                Log.d(WebLoginActivity.class.getSimpleName(), "scheme " + uri.getScheme());
                if (uri.getScheme().equals(getString(R.string.github_oauth_scheme))) {
                    onUserLoggedIn(uri);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        setContentView(webView);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        onUserLoggedIn(uri);
    }

    private void onUserLoggedIn(Uri uri) {
        if (uri != null && uri.getScheme().equals(getString(R.string.github_oauth_scheme))) {
            openLoadingDialog();
            String code = uri.getQueryParameter("code");
            Log.d(WebLoginActivity.class.getSimpleName(), "code " + code);
            if (requestTokenClient == null && code != null) {
                requestTokenClient = new RequestTokenClient(WebLoginActivity.this, code);
                requestTokenClient.setOnResultCallback(new BaseClient.OnResultCallback<Token>() {
                    @Override
                    public void onResponseOk(Token token, Response r) {
                        if (token.access_token != null) {
                            endAuth(token.access_token, token.scope);
                        } else if (token.error != null) {
                            Toast.makeText(WebLoginActivity.this, token.error, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(RetrofitError error) {
                        error.printStackTrace();
                    }
                });
                requestTokenClient.execute();
            }
        }
    }

    @Override
    public void onResponseOk(User user, Response r) {
        Account account = new Account(user.login, getString(R.string.account_type));
        Bundle userData = AccountsHelper.buildBundle(user.name, user.email, user.avatar_url, scope);
        userData.putString(AccountManager.KEY_AUTHTOKEN, accessToken);

        accountManager.addAccountExplicitly(account, null, userData);
        accountManager.setAuthToken(account, getString(R.string.account_type), accessToken);

        Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putString(AccountManager.KEY_AUTHTOKEN, accessToken);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onResult(result);
            mAccountAuthenticatorResponse = null;
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        error.printStackTrace();
    }


    private void openLoadingDialog() {
        progressDialog = LightProgressDialog.create(this,
                R.string.login_activity_authenticating);
        progressDialog.show();
    }

    private void endAuth(String accessToken, String scope) {
        this.accessToken = accessToken;
        this.scope = scope;

        progressDialog.setMessage(getString(R.string.loading_user));

        GetAuthUserClient userClient = new GetAuthUserClient(this, accessToken);
        userClient.setOnResultCallback(this);
        userClient.execute();
    }
}
