package com.test.powerusagesummary;

import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;
import com.android.internal.os.PowerProfile;

import android.content.Context;
import android.os.BatteryStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;

import java.util.List;

/**
 * Created by ptanapaisankit on 1/2/2015.
 */
public class BatteryUsageLollipop {

    private Context mContext;

    private BatteryStatsHelper mStatsHelper;

    private UserManager mUm;

    private static final int MIN_POWER_THRESHOLD_MILLI_AMP = 5;

    private static final int MAX_ITEMS_TO_LIST = 10;

    private static final int MIN_AVERAGE_POWER_THRESHOLD_MILLI_AMP = 10;

    private static final int SECONDS_IN_HOUR = 60 * 60;

    private static final String TAG = BatteryUsageLollipop.class.getSimpleName();

    private int mStatsType = BatteryStats.STATS_SINCE_CHARGED;

    public BatteryUsageLollipop(Context context) throws Exception {
        if (Build.VERSION.SDK_INT < 21) {
            throw new Exception("Call requires API level 21");
        }
        mUm = (UserManager) context.getSystemService(Context.USER_SERVICE);
        mStatsHelper = new BatteryStatsHelper(context, true);
        mStatsHelper.create(new Bundle());
        mStatsHelper.clearStats();
        mContext = context;
    }

    public void refreshStats() {
        boolean addedSome = false;

        final PowerProfile powerProfile = mStatsHelper.getPowerProfile();
        final BatteryStats stats = mStatsHelper.getStats();
        final double averagePower = powerProfile.getAveragePower(PowerProfile.POWER_SCREEN_FULL);
        if (averagePower >= MIN_AVERAGE_POWER_THRESHOLD_MILLI_AMP) {
            final List<UserHandle> profiles = mUm.getUserProfiles();

            mStatsHelper.refreshStats(BatteryStats.STATS_SINCE_CHARGED, profiles);

            final List<BatterySipper> usageList = mStatsHelper.getUsageList();

            final int dischargeAmount = stats != null ? stats.getDischargeAmount(mStatsType) : 0;
            final int numSippers = usageList.size();
            for (int i = 0; i < numSippers; i++) {
                final BatterySipper sipper = usageList.get(i);
                if ((sipper.value * SECONDS_IN_HOUR) < MIN_POWER_THRESHOLD_MILLI_AMP) {
                    continue;
                }
                final double percentOfTotal =
                        ((sipper.value / mStatsHelper.getTotalPower()) * dischargeAmount);
                if (((int) (percentOfTotal + .5)) < 1) {
                    continue;
                }
                if (sipper.drainType == BatterySipper.DrainType.OVERCOUNTED) {
                    // Don't show over-counted unless it is at least 2/3 the size of
                    // the largest real entry, and its percent of total is more significant
                    if (sipper.value < ((mStatsHelper.getMaxRealPower() * 2) / 3)) {
                        continue;
                    }
                    if (percentOfTotal < 10) {
                        continue;
                    }
                    if ("user".equals(Build.TYPE)) {
                        continue;
                    }
                }
                if (sipper.drainType == BatterySipper.DrainType.UNACCOUNTED) {
                    // Don't show over-counted unless it is at least 1/2 the size of
                    // the largest real entry, and its percent of total is more significant
                    if (sipper.value < (mStatsHelper.getMaxRealPower() / 2)) {
                        continue;
                    }
                    if (percentOfTotal < 5) {
                        continue;
                    }
                    if ("user".equals(Build.TYPE)) {
                        continue;
                    }
                }
                final UserHandle userHandle = new UserHandle(UserHandle.getUserId(sipper.getUid()));
                final BatteryEntry entry = new BatteryEntry(mContext, mHandler, mUm, sipper);
                final double percentOfMax = (sipper.value * 100) / mStatsHelper.getMaxPower();
                sipper.percent = percentOfTotal;
                Log.d(TAG, entry.getLabel() + " " + percentOfMax);
                addedSome = true;
            }
        }
        if (!addedSome) {
            Log.d(TAG, "not available");
        }

        BatteryEntry.startRequestQueue();
    }

    static final int MSG_REFRESH_STATS = 100;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BatteryEntry.MSG_REPORT_FULLY_DRAWN:

                    break;
                case MSG_REFRESH_STATS:
                    mStatsHelper.clearStats();
                    refreshStats();
            }
            super.handleMessage(msg);
        }
    };
}
