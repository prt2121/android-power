package com.prt2121.github;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.prt2121.githubsdk.model.response.Repo;
import com.prt2121.githubsdk.service.repos.UserRepos;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import javax.inject.Inject;

public class MainActivity extends RxAppCompatActivity {

  public static String TAG = MainActivity.class.getSimpleName();

  @Inject UserRepos userRepos;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((GitHubApp) getApplication()).getAppComponent().inject(this);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(
        view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show());

    retrieveRepos();
  }

  private void retrieveRepos() {
    userRepos.of("prt2121")
        .sortBy("update")
        .execute()
        .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
        .subscribe(rs -> {
          for (Repo repo : rs) {
            Log.d(TAG, "" + repo.toString());
          }
        }, throwable -> {
          Log.e(TAG, "" + throwable.getLocalizedMessage());
        });
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
