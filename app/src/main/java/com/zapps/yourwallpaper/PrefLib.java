package com.zapps.yourwallpaper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Zimincom on 2017. 8. 28..
 */

public class PrefLib {

    private static String KEY_PREFERENCE = "key_preference";
    private static String ERROR_NOT_INIT = "PrefLib needs init. Consider using PrefLib.init()";
    private static boolean IS_INIT = false;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    public static void init(Context context) {
        pref = context.getSharedPreferences(KEY_PREFERENCE, Context.MODE_PRIVATE);
        IS_INIT = true;
    }

    public static void putString(String key, String value) {
        if (IS_INIT) {
            editor = pref.edit();
            editor.putString(key, value);
            editor.apply();
        } else {
            throw new LibNotInitiatedException(ERROR_NOT_INIT);
        }
    }

    public static String getString(String key, String defaultValue) throws LibNotInitiatedException{
        if (IS_INIT)
            return pref.getString(key, defaultValue);
        else
            throw new LibNotInitiatedException(ERROR_NOT_INIT);
    }

    public static void putBoolean(String key, boolean value) {
        if (IS_INIT) {
            editor = pref.edit();
            editor.putBoolean(key, value);
            editor.apply();
        } else {
            throw new LibNotInitiatedException(ERROR_NOT_INIT);
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        if (IS_INIT)
            return pref.getBoolean(key, defaultValue);
        else
            throw new LibNotInitiatedException(ERROR_NOT_INIT);
    }

    private static class LibNotInitiatedException extends RuntimeException {
        public LibNotInitiatedException (String message) {
            super(message);
        }
    }
}
