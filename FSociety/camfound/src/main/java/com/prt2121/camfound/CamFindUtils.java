package com.prt2121.camfound;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit.RestAdapter;

/**
 * Created by pt2121 on 8/21/15.
 *
 * Utils
 */
public class CamFindUtils {

    public static final String DIRECTORY = "IMG";

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(CamActivity.TAG, "Camera is not available " + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }

    public static File getOutputMediaFile(File base) {
        File dir = new File(base, DIRECTORY);

        // Create the storage directory if it does not exist
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.d(CamActivity.TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(dir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    /** Check if this device has a camera */
    public static boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static CamFindService getCamFindService() {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(CamFindService.CAMFIND_URL)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
        return restAdapter.create(CamFindService.class);
    }

}
