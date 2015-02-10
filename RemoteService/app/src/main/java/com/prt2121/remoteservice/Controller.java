package com.prt2121.remoteservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * <p>Example of explicitly starting and stopping the remove service.
 * This demonstrates the implementation of a service that runs in a different
 * process than the rest of the application, which is explicitly started and stopped
 * as desired.</p>
 */
public class Controller extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.remote_service_controller);

        // Watch for button clicks.
        Button button = (Button) findViewById(R.id.start);
        button.setOnClickListener(mStartListener);
        button = (Button) findViewById(R.id.stop);
        button.setOnClickListener(mStopListener);
    }

    private View.OnClickListener mStartListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Make sure the service is started.  It will continue running
            // until someone calls stopService().
            // We use an action code here, instead of explictly supplying
            // the component name, so that other packages can replace
            // the service.
            startService(new Intent(
                    "com.prt2121.remoteservice.REMOTE_SERVICE"));
        }
    };

    private View.OnClickListener mStopListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Cancel a previous call to startService().  Note that the
            // service will not actually stop at this point if there are
            // still bound clients.
            stopService(new Intent(
                    "com.prt2121.remoteservice.REMOTE_SERVICE"));
        }
    };
}
