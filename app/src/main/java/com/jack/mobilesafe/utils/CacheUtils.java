package com.jack.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jack on 2017/2/28.
 * com.jack.mobilesafe.utils
 */

public class CacheUtils {

    private static final String CONFIG_SP = "config";
    public static final String IS_FIRST_USE = "is_first_use";
    public static final String NO_NEED_UPDATE_VERSION = "no_need_update_version";

    private static SharedPreferences mSP;

    private static SharedPreferences getPreferences(Context context) {
        if (mSP == null) {
            mSP = context.getSharedPreferences(CONFIG_SP, Context.MODE_PRIVATE);
        }
        return mSP;
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = getPreferences(context);
        sp.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = getPreferences(context);
        return sp.getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = getPreferences(context);
        return sp.getBoolean(key, defValue);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = getPreferences(context);
        sp.edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = getPreferences(context);
        return sp.getString(key, null);
    }

    public static String getString(Context context, String key, String defString) {
        SharedPreferences sp = getPreferences(context);
        return sp.getString(key, defString);
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = getPreferences(context);
        sp.edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sp = getPreferences(context);
        return sp.getInt(key, -1);
    }

    public static int getInt(Context context, String key, int defInt) {
        SharedPreferences sp = getPreferences(context);
        return sp.getInt(key, defInt);
    }
}