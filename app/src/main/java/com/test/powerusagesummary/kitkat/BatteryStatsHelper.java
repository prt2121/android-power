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

import com.android.internal.app.IBatteryStats;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.os.PowerProfile;
import com.test.powerusagesummary.R;

import android.content.Context;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.BatteryStats;
import android.os.BatteryStats.Uid;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.telephony.SignalStrength;
import android.util.Log;
import android.util.SparseArray;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A helper class for retrieving the power usage information for all applications and services.
 *
 * The caller must initialize this class as soon as activity object is ready to use (for example,
 * in
 * onAttach() for Fragment), call create() in onCreate() and call destroy() in onDestroy().
 */
public class BatteryStatsHelper {

    public static final int NETWORK_MOBILE_RX_BYTES = 0;

    public static final int NETWORK_MOBILE_TX_BYTES = 1;

    public static final int NETWORK_WIFI_RX_BYTES = 2;

    public static final int NETWORK_WIFI_TX_BYTES = 3;

    private static final boolean DEBUG = false;

    private static final String TAG = BatteryStatsHelper.class.getSimpleName();

    private static BatteryStatsImpl sStatsXfer;

    private IBatteryStats mBatteryInfo;

    private UserManager mUm;

    private BatteryStatsImpl mStats;

    private PowerProfile mPowerProfile;

    private final List<BatterySipper> mUsageList = new ArrayList<BatterySipper>();

    private final List<BatterySipper> mWifiSippers = new ArrayList<BatterySipper>();

    private final List<BatterySipper> mBluetoothSippers = new ArrayList<BatterySipper>();

    private final SparseArray<List<BatterySipper>> mUserSippers
            = new SparseArray<List<BatterySipper>>();

    private final SparseArray<Double> mUserPower = new SparseArray<Double>();

    private int mStatsType = BatteryStats.STATS_SINCE_CHARGED;

    private long mStatsPeriod = 0;

    private double mMaxPower = 1;

    private double mTotalPower;

    private double mWifiPower;

    private double mBluetoothPower;

    // How much the apps together have left WIFI running.
    private long mAppWifiRunning;

    /** Queue for fetching name and icon for an application */
    private ArrayList<BatterySipper> mRequestQueue = new ArrayList<BatterySipper>();

    private Context mContext;

    private Handler mHandler;

    private class NameAndIconLoader extends Thread {

        private boolean mAbort = false;

        public NameAndIconLoader() {
            super("BatteryUsage Icon Loader");
        }

        public void abort() {
            mAbort = true;
        }

        @Override
        public void run() {
            while (true) {
                BatterySipper bs;
                synchronized (mRequestQueue) {
                    if (mRequestQueue.isEmpty() || mAbort) {
                        mHandler.sendEmptyMessage(MSG_REPORT_FULLY_DRAWN);
                        return;
                    }
                    bs = mRequestQueue.remove(0);
                }
                bs.loadNameAndIcon();
            }
        }
    }

    private NameAndIconLoader mRequestThread;

    public BatteryStatsHelper(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    /** Clears the current stats and forces recreating for future use. */
    public void clearStats() {
        mStats = null;
    }

    public BatteryStatsImpl getStats() {
        if (mStats == null) {
            load();
        }
        return mStats;
    }

    public PowerProfile getPowerProfile() {
        return mPowerProfile;
    }

    public void create(Bundle icicle) {
        if (icicle != null) {
            mStats = sStatsXfer;
        }
        mBatteryInfo = IBatteryStats.Stub.asInterface(
                ServiceManager.getService(BatteryStats.SERVICE_NAME));
        mUm = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
        mPowerProfile = new PowerProfile(mContext);
    }

    public void pause() {
        if (mRequestThread != null) {
            mRequestThread.abort();
        }
    }

    public void destroy() {
        BatterySipper.sUidCache.clear();
    }

    /**
     * Refreshes the power usage list.
     *
     * @param includeZeroConsumption whether includes those applications which have consumed very
     *                               little power up till now.
     */
    public void refreshStats(boolean includeZeroConsumption) {
        // Initialize mStats if necessary.
        getStats();

        mMaxPower = 0;
        mTotalPower = 0;
        mWifiPower = 0;
        mBluetoothPower = 0;
        mAppWifiRunning = 0;

        mUsageList.clear();
        mWifiSippers.clear();
        mBluetoothSippers.clear();
        mUserSippers.clear();
        mUserPower.clear();

        processAppUsage(includeZeroConsumption);
        processMiscUsage();

        Collections.sort(mUsageList);

        if (mHandler != null) {
            synchronized (mRequestQueue) {
                if (!mRequestQueue.isEmpty()) {
                    if (mRequestThread != null) {
                        mRequestThread.abort();
                    }
                    mRequestThread = new NameAndIconLoader();
                    mRequestThread.setPriority(Thread.MIN_PRIORITY);
                    mRequestThread.start();
                    mRequestQueue.notify();
                }
            }
        }
    }

    private void processAppUsage(boolean includeZeroConsumption) {
        SensorManager sensorManager = (SensorManager) mContext.getSystemService(
                Context.SENSOR_SERVICE);
        final int which = mStatsType;
        final int speedSteps = mPowerProfile.getNumSpeedSteps();
        final double[] powerCpuNormal = new double[speedSteps];
        final long[] cpuSpeedStepTimes = new long[speedSteps];
        for (int p = 0; p < speedSteps; p++) {
            powerCpuNormal[p] = mPowerProfile.getAveragePower(PowerProfile.POWER_CPU_ACTIVE, p);
        }
        final double mobilePowerPerByte = getMobilePowerPerByte();
        final double wifiPowerPerByte = getWifiPowerPerByte();
        long uSecTime = mStats.computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000, which);
        long appWakelockTime = 0;
        BatterySipper osApp = null;
        mStatsPeriod = uSecTime;
        SparseArray<? extends Uid> uidStats = mStats.getUidStats();
        final int NU = uidStats.size();
        for (int iu = 0; iu < NU; iu++) {
            Uid u = uidStats.valueAt(iu);
            double p; // in mAs
            double power = 0; // in mAs
            double highestDrain = 0;
            String packageWithHighestDrain = null;
            //mUsageList.add(new AppUsage(u.getUid(), new double[] {power}));
            Map<String, ? extends BatteryStats.Uid.Proc> processStats = u.getProcessStats();
            long cpuTime = 0;
            long cpuFgTime = 0;
            long wakelockTime = 0;
            long gpsTime = 0;
            if (DEBUG) {
                Log.i(TAG, "UID " + u.getUid());
            }
            if (processStats.size() > 0) {
                // Process CPU time
                for (Map.Entry<String, ? extends BatteryStats.Uid.Proc> ent
                        : processStats.entrySet()) {
                    Uid.Proc ps = ent.getValue();
                    final long userTime = ps.getUserTime(which);
                    final long systemTime = ps.getSystemTime(which);
                    final long foregroundTime = ps.getForegroundTime(which);
                    cpuFgTime += foregroundTime * 10; // convert to millis
                    final long tmpCpuTime = (userTime + systemTime) * 10; // convert to millis
                    int totalTimeAtSpeeds = 0;
                    // Get the total first
                    for (int step = 0; step < speedSteps; step++) {
                        cpuSpeedStepTimes[step] = ps.getTimeAtCpuSpeedStep(step, which);
                        totalTimeAtSpeeds += cpuSpeedStepTimes[step];
                    }
                    if (totalTimeAtSpeeds == 0) {
                        totalTimeAtSpeeds = 1;
                    }
                    // Then compute the ratio of time spent at each speed
                    double processPower = 0;
                    for (int step = 0; step < speedSteps; step++) {
                        double ratio = (double) cpuSpeedStepTimes[step] / totalTimeAtSpeeds;
                        processPower += ratio * tmpCpuTime * powerCpuNormal[step];
                    }
                    cpuTime += tmpCpuTime;
                    if (DEBUG && processPower != 0) {
                        Log.i(TAG, String.format("process %s, cpu power=%.2f",
                                ent.getKey(), processPower / 1000));
                    }
                    power += processPower;
                    if (packageWithHighestDrain == null
                            || packageWithHighestDrain.startsWith("*")) {
                        highestDrain = processPower;
                        packageWithHighestDrain = ent.getKey();
                    } else if (highestDrain < processPower
                            && !ent.getKey().startsWith("*")) {
                        highestDrain = processPower;
                        packageWithHighestDrain = ent.getKey();
                    }
                }
            }
            if (cpuFgTime > cpuTime) {
                if (DEBUG && cpuFgTime > cpuTime + 10000) {
                    Log.i(TAG, "WARNING! Cputime is more than 10 seconds behind Foreground time");
                }
                cpuTime = cpuFgTime; // Statistics may not have been gathered yet.
            }
            power /= 1000;
            if (DEBUG && power != 0) {
                Log.i(TAG, String.format("total cpu power=%.2f", power));
            }

            // Process wake lock usage
            Map<String, ? extends BatteryStats.Uid.Wakelock> wakelockStats = u.getWakelockStats();
            for (Map.Entry<String, ? extends BatteryStats.Uid.Wakelock> wakelockEntry
                    : wakelockStats.entrySet()) {
                Uid.Wakelock wakelock = wakelockEntry.getValue();
                // Only care about partial wake locks since full wake locks
                // are canceled when the user turns the screen off.
                BatteryStats.Timer timer = wakelock.getWakeTime(BatteryStats.WAKE_TYPE_PARTIAL);
                if (timer != null) {
                    wakelockTime += timer.getTotalTimeLocked(uSecTime, which);
                }
            }
            wakelockTime /= 1000; // convert to millis
            appWakelockTime += wakelockTime;

            // Add cost of holding a wake lock
            p = (wakelockTime
                    * mPowerProfile.getAveragePower(PowerProfile.POWER_CPU_AWAKE)) / 1000;
            power += p;
            if (DEBUG && p != 0) {
                Log.i(TAG, String.format("wakelock power=%.2f", p));
            }

            // Add cost of mobile traffic
            final long mobileRx = getNetworkActivityCount(u, NETWORK_MOBILE_RX_BYTES, mStatsType);
            final long mobileTx = getNetworkActivityCount(u, NETWORK_MOBILE_TX_BYTES, mStatsType);
            p = (mobileRx + mobileTx) * mobilePowerPerByte;
            power += p;
            if (DEBUG && p != 0) {
                Log.i(TAG, String.format("mobile power=%.2f", p));
            }

            // Add cost of wifi traffic
            final long wifiRx = getNetworkActivityCount(u, NETWORK_WIFI_RX_BYTES, mStatsType);
            final long wifiTx = getNetworkActivityCount(u, NETWORK_WIFI_TX_BYTES, mStatsType);
            p = (wifiRx + wifiTx) * wifiPowerPerByte;
            power += p;
            if (DEBUG && p != 0) {
                Log.i(TAG, String.format("wifi power=%.2f", p));
            }

            // Add cost of keeping WIFI running.
            long wifiRunningTimeMs = u.getWifiRunningTime(uSecTime, which) / 1000;
            mAppWifiRunning += wifiRunningTimeMs;
            p = (wifiRunningTimeMs
                    * mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_ON)) / 1000;
            power += p;
            if (DEBUG && p != 0) {
                Log.i(TAG, String.format("wifi running power=%.2f", p));
            }

            // Add cost of WIFI scans
            long wifiScanTimeMs = u.getWifiScanTime(uSecTime, which) / 1000;
            p = (wifiScanTimeMs
                    * mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_SCAN)) / 1000;
            power += p;
            if (DEBUG && p != 0) {
                Log.i(TAG, String.format("wifi scanning power=%.2f", p));
            }
            for (int bin = 0; bin < BatteryStats.Uid.NUM_WIFI_BATCHED_SCAN_BINS; bin++) {
                long batchScanTimeMs = u.getWifiBatchedScanTime(bin, uSecTime, which) / 1000;
                p = (batchScanTimeMs
                        * mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_BATCHED_SCAN, bin));
                power += p;
                if (DEBUG && p != 0) {
                    Log.i(TAG, String.format("wifi batched scanning lvl %d = %.2f", bin, p));
                }
            }

            // Process Sensor usage
            java.lang.reflect.Method method;
            try {
                method = u.getClass().getMethod("getSensorStats");
                Map<Integer, ? extends BatteryStats.Uid.Sensor> sensorStats =
                        (Map<Integer, ? extends BatteryStats.Uid.Sensor>) method.invoke(u);
                //Map<Integer, ? extends BatteryStats.Uid.Sensor> sensorStats = u.getSensorStats();
                for (Map.Entry<Integer, ? extends BatteryStats.Uid.Sensor> sensorEntry
                        : sensorStats.entrySet()) {
                    Uid.Sensor sensor = sensorEntry.getValue();
                    int sensorHandle = sensor.getHandle();
                    BatteryStats.Timer timer = sensor.getSensorTime();
                    long sensorTime = timer.getTotalTimeLocked(uSecTime, which) / 1000;
                    double multiplier = 0;
                    switch (sensorHandle) {
                        case Uid.Sensor.GPS:
                            multiplier = mPowerProfile.getAveragePower(PowerProfile.POWER_GPS_ON);
                            gpsTime = sensorTime;
                            break;
                        default:
                            List<Sensor> sensorList = sensorManager.getSensorList(
                                    android.hardware.Sensor.TYPE_ALL);
                            for (android.hardware.Sensor s : sensorList) {
                                if (s.getHandle() == sensorHandle) {
                                    multiplier = s.getPower();
                                    break;
                                }
                            }
                    }
                    p = (multiplier * sensorTime) / 1000;
                    power += p;
                    if (DEBUG && p != 0) {
                        Log.i(TAG, String.format("sensor %s power=%.2f", sensor.toString(), p));
                    }
                }

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (DEBUG) {
                Log.i(TAG, String.format("UID %d total power=%.2f", u.getUid(), power));
            }

            // Add the app to the list if it is consuming power
            boolean isOtherUser = false;
            final int userId = UserHandle.getUserId(u.getUid());
            if (power != 0 || includeZeroConsumption || u.getUid() == 0) {
                BatterySipper app = new BatterySipper(mContext, mRequestQueue, mHandler,
                        packageWithHighestDrain, DrainType.APP, 0, u,
                        new double[]{power});
                app.cpuTime = cpuTime;
                app.gpsTime = gpsTime;
                app.wifiRunningTime = wifiRunningTimeMs;
                app.cpuFgTime = cpuFgTime;
                app.wakeLockTime = wakelockTime;
                app.mobileRxBytes = mobileRx;
                app.mobileTxBytes = mobileTx;
                app.wifiRxBytes = wifiRx;
                app.wifiTxBytes = wifiTx;
                if (u.getUid() == Process.WIFI_UID) {
                    mWifiSippers.add(app);
                } else if (u.getUid() == Process.BLUETOOTH_UID) {
                    mBluetoothSippers.add(app);
                } else if (userId != UserHandle.myUserId()
                        && UserHandle.getAppId(u.getUid()) >= Process.FIRST_APPLICATION_UID) {
                    isOtherUser = true;
                    List<BatterySipper> list = mUserSippers.get(userId);
                    if (list == null) {
                        list = new ArrayList<BatterySipper>();
                        mUserSippers.put(userId, list);
                    }
                    list.add(app);
                } else {
                    mUsageList.add(app);
                }
                if (u.getUid() == 0) {
                    osApp = app;
                }
            }
            if (power != 0 || includeZeroConsumption) {
                if (u.getUid() == Process.WIFI_UID) {
                    mWifiPower += power;
                } else if (u.getUid() == Process.BLUETOOTH_UID) {
                    mBluetoothPower += power;
                } else if (isOtherUser) {
                    Double userPower = mUserPower.get(userId);
                    if (userPower == null) {
                        userPower = power;
                    } else {
                        userPower += power;
                    }
                    mUserPower.put(userId, userPower);
                } else {
                    if (power > mMaxPower) {
                        mMaxPower = power;
                    }
                    mTotalPower += power;
                }
            }
        }

        // The device has probably been awake for longer than the screen on
        // time and application wake lock time would account for.  Assign
        // this remainder to the OS, if possible.
        if (osApp != null) {
            long wakeTimeMillis = mStats.computeBatteryUptime(
                    SystemClock.uptimeMillis() * 1000, which) / 1000;
            wakeTimeMillis -= appWakelockTime + (mStats.getScreenOnTime(
                    SystemClock.elapsedRealtime(), which) / 1000);
            if (wakeTimeMillis > 0) {
                double power = (wakeTimeMillis
                        * mPowerProfile.getAveragePower(PowerProfile.POWER_CPU_AWAKE)) / 1000;
                if (DEBUG) {
                    Log.i(TAG, "OS wakeLockTime " + wakeTimeMillis + " power " + power);
                }
                osApp.wakeLockTime += wakeTimeMillis;
                osApp.value += power;
                osApp.values[0] += power;
                if (osApp.value > mMaxPower) {
                    mMaxPower = osApp.value;
                }
                mTotalPower += power;
            }
        }
    }

    private void addPhoneUsage(long uSecNow) {
        long phoneOnTimeMs = mStats.getPhoneOnTime(uSecNow, mStatsType) / 1000;
        double phoneOnPower = mPowerProfile.getAveragePower(PowerProfile.POWER_RADIO_ACTIVE)
                * phoneOnTimeMs / 1000;
        addEntry(mContext.getString(R.string.power_phone), DrainType.PHONE, phoneOnTimeMs,
                0, phoneOnPower);
    }

    private void addScreenUsage(long uSecNow) {
        double power = 0;
        long screenOnTimeMs = mStats.getScreenOnTime(uSecNow, mStatsType) / 1000;
        power += screenOnTimeMs * mPowerProfile.getAveragePower(PowerProfile.POWER_SCREEN_ON);
        final double screenFullPower =
                mPowerProfile.getAveragePower(PowerProfile.POWER_SCREEN_FULL);
        for (int i = 0; i < BatteryStats.NUM_SCREEN_BRIGHTNESS_BINS; i++) {
            double screenBinPower = screenFullPower * (i + 0.5f)
                    / BatteryStats.NUM_SCREEN_BRIGHTNESS_BINS;
            long brightnessTime = mStats.getScreenBrightnessTime(i, uSecNow, mStatsType) / 1000;
            power += screenBinPower * brightnessTime;
            if (DEBUG) {
                Log.i(TAG, "Screen bin power = " + (int) screenBinPower + ", time = "
                        + brightnessTime);
            }
        }
        power /= 1000; // To seconds
        addEntry(mContext.getString(R.string.power_screen), DrainType.SCREEN, screenOnTimeMs,
                0, power);
    }

    private void addRadioUsage(long uSecNow) {
        double power = 0;
        final int BINS = SignalStrength.NUM_SIGNAL_STRENGTH_BINS;
        long signalTimeMs = 0;
        for (int i = 0; i < BINS; i++) {
            long strengthTimeMs = mStats.getPhoneSignalStrengthTime(i, uSecNow, mStatsType) / 1000;
            power += strengthTimeMs / 1000
                    * mPowerProfile.getAveragePower(PowerProfile.POWER_RADIO_ON, i);
            signalTimeMs += strengthTimeMs;
        }
        long scanningTimeMs = mStats.getPhoneSignalScanningTime(uSecNow, mStatsType) / 1000;
        power += scanningTimeMs / 1000 * mPowerProfile.getAveragePower(
                PowerProfile.POWER_RADIO_SCANNING);
        BatterySipper bs =
                addEntry(mContext.getString(R.string.power_cell), DrainType.CELL,
                        signalTimeMs, 0, power);
        if (signalTimeMs != 0) {
            bs.noCoveragePercent = mStats.getPhoneSignalStrengthTime(0, uSecNow, mStatsType)
                    / 1000 * 100.0 / signalTimeMs;
        }
    }

    private void aggregateSippers(BatterySipper bs, List<BatterySipper> from, String tag) {
        for (int i = 0; i < from.size(); i++) {
            BatterySipper wbs = from.get(i);
            if (DEBUG) {
                Log.i(TAG, tag + " adding sipper " + wbs + ": cpu=" + wbs.cpuTime);
            }
            bs.cpuTime += wbs.cpuTime;
            bs.gpsTime += wbs.gpsTime;
            bs.wifiRunningTime += wbs.wifiRunningTime;
            bs.cpuFgTime += wbs.cpuFgTime;
            bs.wakeLockTime += wbs.wakeLockTime;
            bs.mobileRxBytes += wbs.mobileRxBytes;
            bs.mobileTxBytes += wbs.mobileTxBytes;
            bs.wifiRxBytes += wbs.wifiRxBytes;
            bs.wifiTxBytes += wbs.wifiTxBytes;
        }
    }

    private void addWiFiUsage(long uSecNow) {
        long onTimeMs = mStats.getWifiOnTime(uSecNow, mStatsType) / 1000;
        long runningTimeMs = mStats.getGlobalWifiRunningTime(uSecNow, mStatsType) / 1000;
        if (DEBUG) {
            Log.i(TAG, "WIFI runningTime=" + runningTimeMs
                    + " app runningTime=" + mAppWifiRunning);
        }
        runningTimeMs -= mAppWifiRunning;
        if (runningTimeMs < 0) {
            runningTimeMs = 0;
        }
        double wifiPower = (onTimeMs * 0 /* TODO */
                * mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_ON)
                + runningTimeMs * mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_ON)) / 1000;
        if (DEBUG) {
            Log.i(TAG, "WIFI power=" + wifiPower + " from procs=" + mWifiPower);
        }
        BatterySipper bs = addEntry(mContext.getString(R.string.power_wifi), DrainType.WIFI,
                runningTimeMs, 0, wifiPower + mWifiPower);
        aggregateSippers(bs, mWifiSippers, "WIFI");
    }

    private void addIdleUsage(long uSecNow) {
        long idleTimeMs = (uSecNow - mStats.getScreenOnTime(uSecNow, mStatsType)) / 1000;
        double idlePower = (idleTimeMs * mPowerProfile.getAveragePower(PowerProfile.POWER_CPU_IDLE))
                / 1000;
        addEntry(mContext.getString(R.string.power_idle), DrainType.IDLE, idleTimeMs,
                0, idlePower);
    }

    private void addBluetoothUsage(long uSecNow) {
        long btOnTimeMs = mStats.getBluetoothOnTime(uSecNow, mStatsType) / 1000;
        double btPower = btOnTimeMs * mPowerProfile.getAveragePower(PowerProfile.POWER_BLUETOOTH_ON)
                / 1000;
        int btPingCount = mStats.getBluetoothPingCount();
        btPower += (btPingCount
                * mPowerProfile.getAveragePower(PowerProfile.POWER_BLUETOOTH_AT_CMD)) / 1000;
        BatterySipper bs = addEntry(mContext.getString(R.string.power_bluetooth),
                DrainType.BLUETOOTH, btOnTimeMs, 0,
                btPower + mBluetoothPower);
        aggregateSippers(bs, mBluetoothSippers, "Bluetooth");
    }

    private void addUserUsage() {
        for (int i = 0; i < mUserSippers.size(); i++) {
            final int userId = mUserSippers.keyAt(i);
            final List<BatterySipper> sippers = mUserSippers.valueAt(i);
            UserInfo info = mUm.getUserInfo(userId);
            Drawable icon;
            String name;
            if (info != null) {
                icon = null;
                name = info != null ? info.name : null;
                if (name == null) {
                    name = Integer.toString(info.id);
                }
                name = mContext.getResources().getString(
                        R.string.running_process_item_user_label, name);
            } else {
                icon = null;
                name = mContext.getResources().getString(
                        R.string.running_process_item_removed_user_label);
            }
            Double userPower = mUserPower.get(userId);
            double power = (userPower != null) ? userPower : 0.0;
            BatterySipper bs = addEntry(name, DrainType.USER, 0, 0, power);
            bs.icon = icon;
            aggregateSippers(bs, sippers, "User");
        }
    }

    private long getNetworkActivityCount(Uid u, int type, int which) {
        java.lang.reflect.Method method;
        try {
            method = u.getClass().getMethod("getNetworkActivityCount", int.class, int.class);
            return (long) method.invoke(u, type, which);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private long getNetworkActivityCount(BatteryStatsImpl stats, int type, int which) {
        java.lang.reflect.Method method;
        try {
            method = mStats.getClass().getMethod("getNetworkActivityCount", int.class, int.class);
            return (long) method.invoke(mStats, type, which);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Return estimated power (in mAs) of sending a byte with the mobile radio.
     */
    private double getMobilePowerPerByte() {
        final long MOBILE_BPS = 200000; // TODO: Extract average bit rates from system
        final double MOBILE_POWER = mPowerProfile.getAveragePower(PowerProfile.POWER_RADIO_ACTIVE)
                / 3600;

        final long mobileRx = getNetworkActivityCount(mStats, NETWORK_MOBILE_RX_BYTES, mStatsType);
        final long mobileTx = getNetworkActivityCount(mStats, NETWORK_MOBILE_TX_BYTES, mStatsType);
        final long mobileData = mobileRx + mobileTx;

        long radioDataUptimeMs = 0;

        java.lang.reflect.Method method;
        try {
            method = mStats.getClass().getMethod("getRadioDataUptime");
            radioDataUptimeMs = (long) method.invoke(mStats) / 1000;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        final long mobileBps = radioDataUptimeMs != 0
                ? mobileData * 8 * 1000 / radioDataUptimeMs
                : MOBILE_BPS;

        return MOBILE_POWER / (mobileBps / 8);
    }

    /**
     * Return estimated power (in mAs) of sending a byte with the Wi-Fi radio.
     */
    private double getWifiPowerPerByte() {
        final long WIFI_BPS = 1000000; // TODO: Extract average bit rates from system
        final double WIFI_POWER = mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_ACTIVE)
                / 3600;
        return WIFI_POWER / (WIFI_BPS / 8);
    }

    private void processMiscUsage() {
        final int which = mStatsType;
        long uSecTime = SystemClock.elapsedRealtime() * 1000;
        final long uSecNow = mStats.computeBatteryRealtime(uSecTime, which);
        final long timeSinceUnplugged = uSecNow;
        if (DEBUG) {
            Log.i(TAG, "Uptime since last unplugged = " + (timeSinceUnplugged / 1000));
        }

        addUserUsage();
        addPhoneUsage(uSecNow);
        addScreenUsage(uSecNow);
        addWiFiUsage(uSecNow);
        addBluetoothUsage(uSecNow);
        addIdleUsage(uSecNow);
        addRadioUsage(uSecNow);
    }

    private BatterySipper addEntry(String label, DrainType drainType, long time, int iconId,
            double power) {
        if (power > mMaxPower) {
            mMaxPower = power;
        }
        mTotalPower += power;
        BatterySipper bs = new BatterySipper(mContext, mRequestQueue, mHandler,
                label, drainType, iconId, null, new double[]{power});
        bs.usageTime = time;
        bs.iconId = iconId;
        mUsageList.add(bs);
        return bs;
    }

    public List<BatterySipper> getUsageList() {
        return mUsageList;
    }

    static final int MSG_UPDATE_NAME_ICON = 1;

    static final int MSG_REPORT_FULLY_DRAWN = 2;

    public double getMaxPower() {
        return mMaxPower;
    }

    public double getTotalPower() {
        return mTotalPower;
    }

    private void load() {
        try {
            byte[] data = mBatteryInfo.getStatistics();
            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(data, 0, data.length);
            parcel.setDataPosition(0);
            mStats = com.android.internal.os.BatteryStatsImpl.CREATOR
                    .createFromParcel(parcel);
            mStats.distributeWorkLocked(BatteryStats.STATS_SINCE_CHARGED);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException:", e);
        }
    }
}
