package com.prt2121.githubsdk.client;

import android.content.Context;
import android.text.TextUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import retrofit.Converter;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static java.lang.String.format;

/**
 * Created by pt2121 on 9/23/15.
 */
public abstract class BaseClient<T> {

  private static final String BASE_URL = "https://api.github.com";
  protected Context context;
  protected String token;
  CredentialStorage storage;

  public BaseClient(Context context) {
    this.context = context.getApplicationContext();
    storage = new CredentialStorage(context);
  }

  public BaseClient(Context context, String token) {
    this.context = context.getApplicationContext();
    this.token = token;
    storage = new CredentialStorage(context);
    storage.storeToken(token);
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

  protected abstract Observable<T> execute();

  protected <T1> Observable.Transformer<T1, T1> applySchedulers() {
    return observable -> observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
