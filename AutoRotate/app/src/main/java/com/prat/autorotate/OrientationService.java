package com.prat.autorotate;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class OrientationService extends Service {

    private View mView;

    private final IBinder mBinder = new OrientationServiceBinder();

    public OrientationService() {
    }

    public class OrientationServiceBinder extends Binder {

        OrientationService getService() {
            return OrientationService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Change the desired orientation.
     *
     * @param orientation An orientation constant as used in
     *                    {@link android.content.pm.ActivityInfo#screenOrientation
     *                    ActivityInfo.screenOrientation}.
     */
    public void forceOrientation(Context context, int orientation) {
        mView = new LinearLayout(context);
        WindowManager.LayoutParams layoutParams =
                new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, 0,
                        PixelFormat.RGBA_8888);
        layoutParams.screenOrientation
                = orientation; //ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
        WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        wm.addView(mView, layoutParams);
        mView.setVisibility(View.VISIBLE);
    }

    public void removeForceOrientation(Context context) {
        if (mView != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
            mView.setVisibility(View.GONE);
            wm.removeView(mView);
            //wm.removeViewImmediate(mView);
        }
        stopSelf();
//        mView = new LinearLayout(context);
//        WindowManager.LayoutParams layoutParams =
//                new WindowManager.LayoutParams(
//                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, 0,
//                        PixelFormat.RGBA_8888);
//        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
//        WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
//        wm.addView(mView, layoutParams);
//        mView.setVisibility(View.VISIBLE);
//
//        wm.removeView(mView);
    }

    public int getAutoOrientationEnabled(Context context) {
        try {
            return Settings.System
                    .getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setAutoOrientationEnabled(Context context, boolean enabled) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION,
                enabled ? 1 : 0);
    }

}
