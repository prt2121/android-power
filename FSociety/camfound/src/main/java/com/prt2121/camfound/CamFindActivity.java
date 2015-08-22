package com.prt2121.camfound;

import com.desmond.squarecamera.CameraActivity;
import com.prt2121.camfound.model.CamFindResult;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.io.File;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by pt2121 on 8/22/15.
 */
public class CamFindActivity extends CameraActivity {

    public static final String CAMFIND_RESULT = "CamFind_result";

    public static String CAMFIND_KEY_EXTRA = "CamFind_key_extra";

    private String mCamFindKey;

    public static void startCamFindActivityForResult(Activity activity,
            String camFindKey,
            int requestCode) {
        if (TextUtils.isEmpty(camFindKey)) {
            // TODO: will improve this?
            throw new IllegalArgumentException("camFindKey can't be null or empty!");
        }

        Intent intent = new Intent(activity, CamFindActivity.class);
        intent.putExtra(CAMFIND_KEY_EXTRA, camFindKey);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            returnAndFinish(RESULT_CANCELED, "Gimme CamFind Key");
            return;
        }

        mCamFindKey = intent.getStringExtra(CAMFIND_KEY_EXTRA);
        if (TextUtils.isEmpty(mCamFindKey)) {
            returnAndFinish(RESULT_CANCELED, "Gimme CamFind Key");
        }
    }

    @Override
    public void returnPhotoUri(Uri uri) {
        showProgress();
        recognizeImage(new File(uri.getPath()));
    }

    private void returnAndFinish(int resultCode, String result) {
        Intent intent = new Intent();
        intent.putExtra(CAMFIND_RESULT, result);
        setResult(resultCode, intent);
        finish();
    }

    private void recognizeImage(File pictureFile) {
        final CamFindService service = CamFindUtils.getCamFindService();
        final ICamFind camFind = new CamFind(mCamFindKey);
        final Observable<CamFindResult> result =
                camFind.getCamFindResultObservable(pictureFile, service);
        camFind.pollCamFindForStatus(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(camFindResult -> returnAndFinish(RESULT_OK, camFindResult.getName()),
                        throwable -> returnAndFinish(RESULT_CANCELED, throwable.getLocalizedMessage()));
    }

    private void showProgress() {
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        ((ViewGroup) findViewById(android.R.id.content)).addView(progressBar, params);
    }
}
