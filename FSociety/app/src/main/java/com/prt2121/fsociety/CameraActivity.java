package com.prt2121.fsociety;

import com.prt2121.fsociety.camfind.CamFind;
import com.prt2121.fsociety.camfind.CamFindService;
import com.prt2121.fsociety.camfind.ICamFind;
import com.prt2121.fsociety.camfind.model.CamFindResult;
import com.prt2121.fsociety.camfind.model.CamFindToken;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CameraActivity extends AppCompatActivity {

    public static final String TAG = CameraActivity.class.getSimpleName();

    public static final String DIRECTORY = "IMG";

    private Camera mCamera;

    private CameraPreview mPreview;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final File pictureFile = getOutputMediaFile(getFilesDir());
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
                        final RestAdapter restAdapter = new RestAdapter.Builder()
                                .setEndpoint(CamFindService.CAMFIND_URL)
                                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                                .build();

                        final CamFindService service = restAdapter.create(CamFindService.class);
                        final ICamFind camFind = new CamFind();

                        final Observable<CamFindResult> result =
                                camFind.postImage(service, pictureFile.getAbsolutePath())
                                        .map(new Func1<CamFindToken, String>() {
                                            @Override
                                            public String call(CamFindToken camFindToken) {
                                                return camFindToken.getToken();
                                            }
                                        })
                                        .cache()
                                        .flatMap(new Func1<String, Observable<CamFindResult>>() {
                                            @Override
                                            public Observable<CamFindResult> call(String s) {
                                                return camFind.getCamFindImageResponse(service, s);
                                            }
                                        });

                        // http://blog.freeside.co/2015/01/29/simple-background-polling-with-rxjava/
                        // http://stackoverflow.com/questions/28369689/android-polling-a-server-with-retrofit
                        Observable.interval(12, TimeUnit.SECONDS, Schedulers.io())
                                .startWith(-1L)
                                .flatMap(new Func1<Long, Observable<CamFindResult>>() {
                                    @Override
                                    public Observable<CamFindResult> call(Long tick) {
                                        Log.d("Retrofit", "token " + tick);
                                        return result;
                                    }
                                })
                                .filter(new Func1<CamFindResult, Boolean>() {
                                    @Override
                                    public Boolean call(CamFindResult camFindResult) {
                                        return camFindResult.getStatus().equalsIgnoreCase("completed");
                                    }
                                })
                                .take(1)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<CamFindResult>() {
                                    @Override
                                    public void call(CamFindResult camFindResult) {
                                        Log.d(TAG, camFindResult.getName());
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        Log.d(TAG, throwable.getLocalizedMessage());
                                    }
                                });
                    }
                }
            }).start();

            if (mCamera != null) {
                mCamera.startPreview();
            }
        }

    };

    private FrameLayout mFrameLayout;

    private Button mCaptureButton;

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "Camera is not available " + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }

    private static File getOutputMediaFile(File base) {
        File dir = new File(base, DIRECTORY);

        // Create the storage directory if it does not exist
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(dir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (checkCameraHardware(this)) {
            // Create an instance of Camera
            mCamera = getCameraInstance();
        }
        mFrameLayout = (FrameLayout) findViewById(R.id.camera_preview);
        mCaptureButton = (Button) findViewById(R.id.button_capture);
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
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
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // get an image from the camera
                            mCamera.takePicture(null, null, mPicture);
                        }
                    }
            );
        }
    }

}

//                                .flatMap(new Func1<Long, Observable<CamFindResult>>() {
//                                    @Override
//                                    public Observable<CamFindResult> call(Long aLong) {
//                                        return tokenObservable.flatMap(new Func1<String, Observable<CamFindResult>>() {
//                                            @Override
//                                            public Observable<CamFindResult> call(String s) {
//                                                return camFind.getCamFindImageResponse(service, s);
//                                            }
//                                        });
//                                    }
//                                })
//                                .filter(/* check if it is a valid new game state */)
//                                .take(1)

//                        Observable.interval(30, TimeUnit.SECONDS, Schedulers.io())
//                                .map(tick -> messageService.getRecentMessages())
//                                .doOnError(err -> log.error("Error retrieving messages", err))
//                                .retry()
//                                .flatMap(Observable::from)
//                                        // filter out any previously seen messages
//                                .distinct()
//                                .filter(message -> message.isFor(recipient))
//                                .subscribe(message -> log.info(message.toString()));
