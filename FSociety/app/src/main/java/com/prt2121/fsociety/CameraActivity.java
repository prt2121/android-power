package com.prt2121.fsociety;

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
