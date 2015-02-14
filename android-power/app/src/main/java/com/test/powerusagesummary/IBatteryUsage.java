package com.test.powerusagesummary;

import android.util.Pair;

import java.util.List;

/**
 * Created by prat on 1/2/2015.
 */
public interface IBatteryUsage {

    /**
     * Get the power usage list.
     * @return the list of pairs of a package name and usage percentage.
     */
    public List<Pair<String, Float>> getUsages();

}
