package com.prt2121.camfound;

import com.prt2121.camfound.model.CamFindResult;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by pt2121 on 8/17/15.
 *
 * This activity is used to capture a photo.
 */
public class CameraActivity extends AppCompatActivity {

    public static final String TAG = CameraActivity.class.getSimpleName();

    public static final String CAMFIND_RESULT = "CamFind_result";

    private Camera mCamera;

    private CameraPreview mPreview;

    private FrameLayout mFrameLayout;

    private FloatingActionButton mCaptureButton;

    public static String CAMFIND_KEY_EXTRA = "CamFind_key_extra";

    private String mCamFindKey;

    public static void startCameraActivityForResult(Activity activity,
            String camFindKey,
            int requestCode) {
        if (TextUtils.isEmpty(camFindKey)) {
            // TODO: will improve this?
            throw new IllegalArgumentException("camFindKey can't be null or empty!");
        }

        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra(CAMFIND_KEY_EXTRA, camFindKey);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Intent intent = getIntent();
        if (intent == null) {
            returnAndFinish(RESULT_CANCELED, "Gimme CamFind Key");
            return;
        }

        mCamFindKey = intent.getStringExtra(CAMFIND_KEY_EXTRA);
        if (TextUtils.isEmpty(mCamFindKey)) {
            returnAndFinish(RESULT_CANCELED, "Gimme CamFind Key");
        }

        if (CamFindUtils.checkCameraHardware(this)) {
            mCamera = CamFindUtils.getCameraInstance();
        }
        mFrameLayout = (FrameLayout) findViewById(R.id.camera_preview);
        mCaptureButton = (FloatingActionButton) findViewById(R.id.button_capture);

        if (mCamera == null) {
            mCaptureButton.setVisibility(View.GONE);
        }
    }

    private void returnAndFinish(int resultCode, String result) {
        Intent intent = new Intent();
        intent.putExtra(CAMFIND_RESULT, result);
        setResult(resultCode, intent);
        finish();
    }

    private void releaseCameraAndPreview() {
        if (mPreview != null) {
            mFrameLayout.removeView(mPreview);
            mPreview.setCamera(null);
            mPreview = null;
        }
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCameraAndPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Create our Preview view and set it as the content of our activity.
        if (mCamera != null) {
            mPreview = new CameraPreview(this, mCamera);
            mFrameLayout.addView(mPreview);
            mCaptureButton.setOnClickListener(
                    v -> {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
            );
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            releaseCameraAndPreview();

//            ImageUtils.decodeSampledBitmapFromByte(CameraActivity.this, data);
//
//            ResizeAnimation animation = new ResizeAnimation.Builder().setView(mFrameLayout)
//                    .setFromHeight(mFrameLayout.getHeight())
//                    .setFromWidth(mFrameLayout.getWidth())
//                    .setToHeight(mFrameLayout.getHeight() * 3 / 4)
//                    .setToWidth(mFrameLayout.getWidth() * 3 / 4)
//                    .createResizeAnimation();
//            mFrameLayout.startAnimation(animation);

            new Thread(() -> {
                final File pictureFile = CamFindUtils.getOutputMediaFile(getFilesDir());
                if (pictureFile == null) {
                    Log.d(TAG, "Error creating media file.");
                    return;
                }
                Log.d(TAG, pictureFile.getAbsolutePath());
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                } finally {
                    recognizeImage(pictureFile);
                }
            }).start();

//            if (mCamera != null) {
//                mCamera.startPreview();
//            }
        }

    };

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

}