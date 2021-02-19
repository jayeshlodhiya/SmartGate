package com.payphi.visitorsregister.utils;

import android.os.Handler;
import android.util.Log;

public class ActivityState {
    private static boolean isInBackground;
    private static boolean isAwakeFromBackground;
    private static final int backgroundAllowance = 10000;
    public static void activityPaused() {
        isInBackground = true;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isInBackground) {
                    isAwakeFromBackground = true;
                }
            }
        }, backgroundAllowance);
        Log.v("activity status", "activityPaused");
    }

    public static void activityResumed() {
        isInBackground = false;
        if(isAwakeFromBackground){
            // do something when awake from background
            Log.v("activity status", "isAwakeFromBackground");
        }
        isAwakeFromBackground = false;
        Log.v("activity status", "activityResumed");
    }
}
