package com.test.powerusagesummary;

import com.android.internal.os.BatteryStatsHelper;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    BatteryStatsHelper batteryStatsHelper;

    private IBatteryUsage mBatteryUsage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBatteryUsage = BatteryUsageFactory.create(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            List<Pair<String, Float>> list = mBatteryUsage.getUsages();
            for (Pair<String, Float> pair : list) {
                Log.d(MainActivity.class.getSimpleName(), " " + pair.first + " " + pair.second);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
