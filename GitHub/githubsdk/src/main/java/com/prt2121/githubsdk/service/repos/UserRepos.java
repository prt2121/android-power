package com.prt2121.githubsdk.service.repos;

import android.content.Context;
import android.text.TextUtils;
import com.prt2121.githubsdk.client.BaseClient;
import com.prt2121.githubsdk.model.response.Repo;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;

/**
 * Created by pt2121 on 9/23/15.
 */
public class UserRepos extends BaseClient<List<Repo>> {

  private String username;

  private String sort;
  private static final String DEFAULT_SORT = "update";

  @Inject public UserRepos(Context context) {
    super(context);
  }

  public UserRepos of(String username) {
    this.username = username;
    return this;
  }

  public UserRepos sortBy(String sort) {
    this.sort = sort;
    return this;
  }

  public Observable<List<Repo>> execute() {
    ReposService service = getRetrofit().create(ReposService.class);
    if (TextUtils.isEmpty(username) && TextUtils.isEmpty(sort)) {
      return service.repos(DEFAULT_SORT).compose(this.<List<Repo>>applySchedulers());
    } else if (TextUtils.isEmpty(username)) {
      return service.repos(sort).compose(this.<List<Repo>>applySchedulers());
    } else {
      return service.repos(username, sort).compose(this.<List<Repo>>applySchedulers());
    }
  }
}
