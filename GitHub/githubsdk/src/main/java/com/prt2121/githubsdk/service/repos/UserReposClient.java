package com.prt2121.githubsdk.service.repos;

import android.content.Context;
import com.prt2121.githubsdk.client.BaseClient;
import com.prt2121.githubsdk.model.response.Repo;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;

/**
 * Created by pt2121 on 9/23/15.
 */
public class UserReposClient extends BaseClient<List<Repo>> {

  @Inject public UserReposClient(Context context) {
    super(context);
  }

  @Override public Observable<List<Repo>> execute() {
    return execute("prt2121", "updated");
  }

  private Observable<List<Repo>> execute(String username, String sort) {
    ReposService service = getRetrofit().create(ReposService.class);
    return service.repos(username, sort).compose(this.<List<Repo>>applySchedulers());
  }
}
