package com.prt2121.githubsdk.service.auth;

import android.content.Context;
import com.prt2121.githubsdk.R;
import com.prt2121.githubsdk.client.BaseClient;
import com.prt2121.githubsdk.model.request.RequestTokenDTO;
import com.prt2121.githubsdk.model.response.Token;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;

/**
 * Created by pt2121 on 9/24/15.
 */
@Singleton
public class RequestToken extends BaseClient {

  public static final String AUTH_URL = "https://github.com";
  private String code;
  Context context;

  @Inject public RequestToken(Context context) {
    this.context = context;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Observable<Token> token() {
    Retrofit.Builder builder =
        new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .baseUrl(AUTH_URL);
    LoginService service = builder.client(getClient()).build().create(LoginService.class);

    RequestTokenDTO tokenDTO = new RequestTokenDTO();
    tokenDTO.client_id = context.getResources().getString(R.string.gh_client_id);
    tokenDTO.client_secret = context.getResources().getString(R.string.gh_client_secret);
    tokenDTO.redirect_uri = context.getResources().getString(R.string.gh_client_callback);
    tokenDTO.code = code;

    return service.token(tokenDTO);
  }

  @Override protected OkHttpClient getClient() {
    OkHttpClient client = new OkHttpClient();
    client.interceptors().add(chain -> {
      Request request = chain.request();
      Request newReq = request.newBuilder().addHeader("Accept", "application/json").build();
      return chain.proceed(newReq);
    });
    return client;
  }
}
