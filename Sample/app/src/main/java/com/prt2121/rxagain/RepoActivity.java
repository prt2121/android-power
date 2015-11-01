package com.prt2121.rxagain;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.prt2121.rxagain.model.Repo;
import com.prt2121.rxagain.model.SearchResult;
import java.lang.reflect.Type;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class RepoActivity extends AppCompatActivity {

  @Bind(R.id.repoToolbar) Toolbar toolbar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_repo);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);

    final Observable<Void> menuItemClicks =
        RxToolbar.itemClicks(toolbar).filter(new Func1<MenuItem, Boolean>() {
          @Override public Boolean call(MenuItem menuItem) {
            Timber.d(menuItem.getTitle().toString());
            return menuItem.getItemId() == R.id.action_refresh;
          }
        }).map(new Func1<MenuItem, Void>() {
          @Override public Void call(MenuItem menuItem) {
            Timber.d(menuItem.getTitle().toString());
            return null;
          }
        });

    menuItemClicks.startWith((Void) null).map(new Func1<Void, String>() {
      @Override public String call(Void v) {
        return "https://api.github.com/search/repositories?q=reactive";
      }
    }).flatMap(new Func1<String, Observable<String>>() {
      @Override public Observable<String> call(String url) {
        return RxApp.getApp().requestJson(url);
      }
    }).map(new Func1<String, List<Repo>>() {
      @Override public List<Repo> call(String json) {
        Type type = new TypeToken<SearchResult>() {
        }.getType();
        SearchResult searchResult = new GsonBuilder().create().fromJson(json, type);
        return searchResult.getRepos();
      }
    }).subscribe(new Action1<List<Repo>>() {
      @Override public void call(List<Repo> repoList) {
        for (Repo repo : repoList) {
          Timber.d(repo.getOwner().getLogin());
        }
      }
    }, new Action1<Throwable>() {
      @Override public void call(Throwable throwable) {
        Timber.d(throwable.getMessage());
      }
    });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_repo, menu);
    return true;
  }
}
