package com.prt2121.github;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.prt2121.githubsdk.model.response.Repo;
import com.prt2121.githubsdk.service.repos.ReposService;
import java.util.List;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  public static String TAG = MainActivity.class.getSimpleName();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(
        view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show());

    Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .baseUrl("https://api.github.com")
        .build();

    ReposService service = retrofit.create(ReposService.class);
    service.repos("prt2121").compose(this.<List<Repo>>applySchedulers()).subscribe(repos -> {
      for (Repo repo : repos) {
        Log.d(TAG, "" + repo.toString());
      }
    }, throwable -> {
      Log.e(TAG, "" + throwable.getLocalizedMessage());
    });
  }

  <T> Observable.Transformer<T, T> applySchedulers() {
    return observable -> observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
