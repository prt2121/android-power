package com.prt2121.githubsdk.client;

import android.text.TextUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import javax.inject.Inject;
import retrofit.Converter;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

import static java.lang.String.format;

/**
 * Created by pt2121 on 9/23/15.
 */
public abstract class BaseClient {

  private static final String BASE_URL = "https://api.github.com";
  @Inject public CredentialStorage storage;
  protected String token;

  public BaseClient() {
  }

  protected Retrofit getRetrofit() {
    Retrofit.Builder builder =
        new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .baseUrl(BASE_URL);

    if (getClient() != null) {
      builder.client(getClient());
    } else if (storage != null && !TextUtils.isEmpty(storage.token())) {
      token = storage.token();
      OkHttpClient client = new OkHttpClient();
      client.interceptors().add(chain -> {
        Request request = chain.request();
        Request newReq =
            request.newBuilder().addHeader("Authorization", format("token %s", token)).build();
        return chain.proceed(newReq);
      });
      builder.client(client);
    }

    if (customConverter() != null) {
      builder.addConverterFactory(customConverter());
    }

    return builder.build();
  }

  protected Converter.Factory customConverter() {
    return null;
  }

  protected OkHttpClient getClient() {
    return null;
  }
}
