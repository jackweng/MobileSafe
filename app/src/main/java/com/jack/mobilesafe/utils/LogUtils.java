package com.jack.mobilesafe.utils;

import android.util.Log;

import static com.jack.mobilesafe.debugconfig.DebugConfig.isDebug;

/**
 * Created by Jack on 2017/2/26.
 * utils
 */

public class LogUtils {

    private static final String TAG = "com.jack.mobilesafe";

    public static final void LogI(String text) {
        if (isDebug) {
            Log.i(TAG, text);
        }
    }
}
