package com.prt2121.github;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.prt2121.githubsdk.model.response.Repo;
import com.prt2121.githubsdk.service.event.Event;
import com.prt2121.githubsdk.service.repos.UserRepos;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

public class MainActivity extends RxAppCompatActivity {

  @Inject UserRepos userRepos;
  @Inject Event event;

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
    retrievePrivateEvents();
  }

  private void retrievePrivateEvents() {
    event.forUser("prt2121")
        .events()
        .flatMap(Observable::from)
        .filter(e -> !e.publicEvent)
        .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
        .compose(RxUtils.applySchedulers())
        .subscribe(es -> {
          Timber.d("event " + es.toString());
        }, throwable -> {
          Timber.d(throwable.getLocalizedMessage());
        });
  }

  private void retrieveRepos() {
    userRepos.of("prt2121")
        .sortBy("update")
        .repos()
        .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
        .compose(RxUtils.applySchedulers())
        .subscribe(rs -> {
          for (Repo repo : rs) {
            Timber.d(repo.toString());
          }
        }, throwable -> {
          Timber.d(throwable.getLocalizedMessage());
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
