/*
* Copyright (C) 2009 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.test.powerusagesummary.kitkat;

import com.android.internal.os.PowerProfile;
import com.test.powerusagesummary.IBatteryUsage;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prat on 1/2/2015.
 */
public class BatteryUsageKitKat implements IBatteryUsage {

    private Context mContext;

    private BatteryStatsHelper mStatsHelper;

    private UserManager mUm;

    private static final int MIN_POWER_THRESHOLD = 5;

    public BatteryUsageKitKat(Context context) {
        mContext = context;
        mUm = (UserManager) context.getSystemService(Context.USER_SERVICE);
        mStatsHelper = new BatteryStatsHelper(context, mHandler);
        mStatsHelper.create(new Bundle());
        mStatsHelper.clearStats();
    }

    @Override
    public List<Pair<String, Float>> getUsages() {
        List<Pair<String, Float>> mUsages = new ArrayList<Pair<String, Float>>();
        if (mStatsHelper.getPowerProfile().getAveragePower(
                PowerProfile.POWER_SCREEN_FULL) < 10) {
            return mUsages;
        }
        mStatsHelper.refreshStats(false);
        List<BatterySipper> usageList = mStatsHelper.getUsageList();
        for (BatterySipper sipper : usageList) {
            if (sipper.getSortValue() < MIN_POWER_THRESHOLD) {
                continue;
            }
            final double percentOfTotal =
                    ((sipper.getSortValue() / mStatsHelper.getTotalPower()) * 100);
            if (percentOfTotal < 1) {
                continue;
            }
            final double percentOfMax =
                    (sipper.getSortValue() * 100) / mStatsHelper.getMaxPower();
            sipper.percent = percentOfTotal;
            mUsages.add(new Pair<String, Float>(sipper.getLabel(), (float) percentOfTotal));
        }
        return mUsages;
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BatteryStatsHelper.MSG_UPDATE_NAME_ICON:
                    break;
                case BatteryStatsHelper.MSG_REPORT_FULLY_DRAWN:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
