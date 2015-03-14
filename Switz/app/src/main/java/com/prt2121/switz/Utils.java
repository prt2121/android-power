package com.prt2121.switz;

/**
 * Created by pt2121 on 3/13/15.
 */
public class Utils {

    public static boolean[] toBinary(int number, int base) {
        final boolean[] ret = new boolean[base];
        for (int i = 0; i < base; i++) {
            ret[base - 1 - i] = (1 << i & number) != 0;
        }
        return ret;
    }

}
