package com.prt2121.fsociety;

import com.prt2121.camfound.CameraActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CameraActivity.startCameraActivityForResult(this, getString(R.string.key), 777);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        String result = data.getStringExtra(CameraActivity.CAMFIND_RESULT);
        Log.d(TAG, result);

        MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        fragment.setText(result);
        if (requestCode == 777) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, result);
            } else {
                Log.e(TAG, result);
            }
        }
    }

}
