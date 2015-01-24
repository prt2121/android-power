package com.prat.autorotate;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;


public class MainActivity extends Activity {

    boolean mIsBound = false;

//    @Override
//    protected void onDestroy() {
//        removeForceOrientation(this);
//        mView = null;
//        super.onDestroy();
//    }

    private OrientationService mBoundService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((OrientationService.OrientationServiceBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        doBindService();
        ToggleButton mButton = (ToggleButton) findViewById(R.id.button);
        ToggleButton mToggleButton = (ToggleButton) findViewById(R.id.toggleButton);

        mButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mIsBound && mBoundService != null) {
                        mBoundService.setAutoOrientationEnabled(getApplicationContext(), false);
                    }
                } else {
                    if (mIsBound && mBoundService != null) {
                        mBoundService.setAutoOrientationEnabled(getApplicationContext(), true);
                    }
                }
            }
        });

        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mIsBound && mBoundService != null) {
                        mBoundService.forceOrientation(getApplicationContext(),
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                } else {
                    if (mIsBound && mBoundService != null) {
                        mBoundService.removeForceOrientation(getApplicationContext());
                    }
                }
            }
        });
//                Log.d(MainActivity.class.getSimpleName(),
//                        "AutoOrientationEnabled "
//                                + getAutoOrientationEnabled(MainActivity.this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void doBindService() {
        bindService(new Intent(MainActivity.this,
                OrientationService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}



//
//    /**
//     * Change the desired orientation.
//     *
//     * @param orientation An orientation constant as used in
//     *                    {@link android.content.pm.ActivityInfo#screenOrientation
//     *                    ActivityInfo.screenOrientation}.
//     */
//    public void forceOrientation(Context context, int orientation) {
//        mView = new LinearLayout(context);
//        WindowManager.LayoutParams layoutParams =
//                new WindowManager.LayoutParams(
//                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, 0,
//                        PixelFormat.RGBA_8888);
//        layoutParams.screenOrientation
//                = orientation; //ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
//        WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
//        wm.addView(mView, layoutParams);
//        mView.setVisibility(View.VISIBLE);
//    }
//
//    public void removeForceOrientation(Context context) {
//        if (mView != null) {
//            WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
//            mView.setVisibility(View.GONE);
//            wm.removeView(mView);
//            //wm.removeViewImmediate(mView);
//        }
//    }
//
//    public int getAutoOrientationEnabled(Context context) {
//        try {
//            return Settings.System
//                    .getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//            return -1;
//        }
//    }
//
//    public void setAutoOrientationEnabled(Context context, boolean enabled) {
//        Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION,
//                enabled ? 1 : 0);
//    }
