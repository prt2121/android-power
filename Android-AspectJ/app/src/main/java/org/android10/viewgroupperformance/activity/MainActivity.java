package org.android10.viewgroupperformance.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import org.android10.gintonic.annotation.DebugTrace;
import org.android10.viewgroupperformance.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

  private Button btnRelativeLayoutTest;
  private Button btnLinearLayoutTest;
  private Button btnFrameLayoutTest;

  @DebugTrace @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mapGUI();

    Observable.interval(1, TimeUnit.SECONDS)
        .startWith(-1L)
        .map(new Func1<Long, Integer>() {
          @Override public Integer call(Long l) {
            return new BigDecimal(l).intValueExact();
          }
        })
        .takeUntil(new Func1<Integer, Boolean>() {
          @Override public Boolean call(Integer l) {
            return l > 5;
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Integer>() {
          @Override public void call(Integer l) {
            Log.d(MainActivity.class.getSimpleName(), "l " + l);
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {
            Log.e(MainActivity.class.getSimpleName(), throwable.getMessage());
          }
        }, new Action0() {
          @Override public void call() {
            hello("prat");
          }
        });
  }

  private String hello(String name) {
    return "hello, " + name;
  }

  /**
   * Maps Graphical User Interface
   */
  private void mapGUI() {
    this.btnRelativeLayoutTest = (Button) findViewById(R.id.btnRelativeLayout);
    this.btnRelativeLayoutTest.setOnClickListener(btnRelativeLayoutOnClickListener);

    this.btnLinearLayoutTest = (Button) findViewById(R.id.btnLinearLayout);
    this.btnLinearLayoutTest.setOnClickListener(btnLinearLayoutOnClickListener);

    this.btnFrameLayoutTest = (Button) findViewById(R.id.btnFrameLayout);
    this.btnFrameLayoutTest.setOnClickListener(btnFrameLayoutOnClickListener);
  }

  private View.OnClickListener btnRelativeLayoutOnClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      openActivity(RelativeLayoutTestActivity.class);
    }
  };

  private View.OnClickListener btnLinearLayoutOnClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      openActivity(LinearLayoutTestActivity.class);
    }
  };

  private View.OnClickListener btnFrameLayoutOnClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      openActivity(FrameLayoutTestActivity.class);
    }
  };

  /**
   * Open and activity
   */
  private void openActivity(Class activityToOpen) {
    Intent intent = new Intent(this, activityToOpen);
    startActivity(intent);
  }
}
