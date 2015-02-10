package com.prt2121.remoteservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Examples of behavior of different bind flags.</p>
 */
// BEGIN_INCLUDE(calling_a_service)
public class BindingOptions extends Activity {

    ServiceConnection mCurConnection;

    TextView mCallbackText;

    class MyServiceConnection implements ServiceConnection {

        final boolean mUnbindOnDisconnect;

        public MyServiceConnection() {
            mUnbindOnDisconnect = false;
        }

        public MyServiceConnection(boolean unbindOnDisconnect) {
            mUnbindOnDisconnect = unbindOnDisconnect;
        }

        public void onServiceConnected(ComponentName className,
                IBinder service) {
            if (mCurConnection != this) {
                return;
            }
            mCallbackText.setText("Attached.");
            Toast.makeText(BindingOptions.this, R.string.remote_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            if (mCurConnection != this) {
                return;
            }
            mCallbackText.setText("Disconnected.");
            Toast.makeText(BindingOptions.this, R.string.remote_service_disconnected,
                    Toast.LENGTH_SHORT).show();
            if (mUnbindOnDisconnect) {
                unbindService(this);
                mCurConnection = null;
                Toast.makeText(BindingOptions.this, R.string.remote_service_unbind_disconn,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Standard initialization of this activity.  Set up the UI, then wait
     * for the user to poke it before doing anything.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.remote_binding_options);

        // Watch for button clicks.
        Button button = (Button) findViewById(R.id.bind_normal);
        button.setOnClickListener(mBindNormalListener);
        button = (Button) findViewById(R.id.bind_not_foreground);
        button.setOnClickListener(mBindNotForegroundListener);
        button = (Button) findViewById(R.id.bind_above_client);
        button.setOnClickListener(mBindAboveClientListener);
        button = (Button) findViewById(R.id.bind_allow_oom);
        button.setOnClickListener(mBindAllowOomListener);
        button = (Button) findViewById(R.id.bind_waive_priority);
        button.setOnClickListener(mBindWaivePriorityListener);
        button = (Button) findViewById(R.id.bind_important);
        button.setOnClickListener(mBindImportantListener);
        button = (Button) findViewById(R.id.bind_with_activity);
        button.setOnClickListener(mBindWithActivityListener);
        button = (Button) findViewById(R.id.unbind);
        button.setOnClickListener(mUnbindListener);

        mCallbackText = (TextView) findViewById(R.id.callback);
        mCallbackText.setText("Not attached.");
    }

    private View.OnClickListener mBindNormalListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mCurConnection != null) {
                unbindService(mCurConnection);
                mCurConnection = null;
            }
            ServiceConnection conn = new MyServiceConnection();
            if (bindService(new Intent(IRemoteService.class.getName()),
                    conn, Context.BIND_AUTO_CREATE)) {
                mCurConnection = conn;
            }
        }
    };

    private View.OnClickListener mBindNotForegroundListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mCurConnection != null) {
                unbindService(mCurConnection);
                mCurConnection = null;
            }
            ServiceConnection conn = new MyServiceConnection();
            if (bindService(new Intent(IRemoteService.class.getName()),
                    conn, Context.BIND_AUTO_CREATE | Context.BIND_NOT_FOREGROUND)) {
                mCurConnection = conn;
            }
        }
    };

    private View.OnClickListener mBindAboveClientListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mCurConnection != null) {
                unbindService(mCurConnection);
                mCurConnection = null;
            }
            ServiceConnection conn = new MyServiceConnection();
            if (bindService(new Intent(IRemoteService.class.getName()),
                    conn, Context.BIND_AUTO_CREATE | Context.BIND_ABOVE_CLIENT)) {
                mCurConnection = conn;
            }
        }
    };

    private View.OnClickListener mBindAllowOomListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mCurConnection != null) {
                unbindService(mCurConnection);
                mCurConnection = null;
            }
            ServiceConnection conn = new MyServiceConnection();
            if (bindService(new Intent(IRemoteService.class.getName()),
                    conn, Context.BIND_AUTO_CREATE | Context.BIND_ALLOW_OOM_MANAGEMENT)) {
                mCurConnection = conn;
            }
        }
    };

    private View.OnClickListener mBindWaivePriorityListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mCurConnection != null) {
                unbindService(mCurConnection);
                mCurConnection = null;
            }
            ServiceConnection conn = new MyServiceConnection(true);
            if (bindService(new Intent(IRemoteService.class.getName()),
                    conn, Context.BIND_AUTO_CREATE | Context.BIND_WAIVE_PRIORITY)) {
                mCurConnection = conn;
            }
        }
    };

    private View.OnClickListener mBindImportantListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mCurConnection != null) {
                unbindService(mCurConnection);
                mCurConnection = null;
            }
            ServiceConnection conn = new MyServiceConnection();
            if (bindService(new Intent(IRemoteService.class.getName()),
                    conn, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT)) {
                mCurConnection = conn;
            }
        }
    };

    private View.OnClickListener mBindWithActivityListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mCurConnection != null) {
                unbindService(mCurConnection);
                mCurConnection = null;
            }
            ServiceConnection conn = new MyServiceConnection();
            if (bindService(new Intent(IRemoteService.class.getName()),
                    conn, Context.BIND_AUTO_CREATE | Context.BIND_ADJUST_WITH_ACTIVITY
                            | Context.BIND_WAIVE_PRIORITY)) {
                mCurConnection = conn;
            }
        }
    };

    private View.OnClickListener mUnbindListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mCurConnection != null) {
                unbindService(mCurConnection);
                mCurConnection = null;
            }
        }
    };
}
