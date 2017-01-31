package edu.stanford.algorithms;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Just some utility functions
 */
public class Utility {

    public static int getDeviceHeight(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }
}
