package com.test.powerusagesummary;

import com.test.powerusagesummary.kitkat.BatteryUsageKitKat;
import com.test.powerusagesummary.lollipop.BatteryUsageLollipop;

import android.content.Context;
import android.os.Build;

/**
 * Created by prat on 1/2/2015.
 */
public class BatteryUsageFactory {

    /**
     * A factory method for creating BatteryUsage based on API level
     * @param context Android context
     * @return BatteryUsage or null if we don't support that API level.
     */
    public static IBatteryUsage create(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new BatteryUsageLollipop(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return new BatteryUsageKitKat(context);
        }
        return null;
    }

}
