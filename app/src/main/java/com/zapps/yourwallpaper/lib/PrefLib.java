package com.zapps.yourwallpaper.lib;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import static com.zapps.yourwallpaper.Constants.KEY_PREFERENCE;

public class PrefLib {

    private static PrefLib ourInstance;
    SharedPreferences pref;

    public static PrefLib getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new PrefLib(context);
        }
        return ourInstance;
    }

    private PrefLib(Context context) {
        pref = context.getSharedPreferences(KEY_PREFERENCE, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return pref.getString(key, defaultValue);
    }

    public void putStringSet(String key, Set<String> values) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(key, values);
        editor.apply();
    }

    public Set<String> getStringSet(String key, Set<String> defaultValues) {
        return pref.getStringSet(key, defaultValues);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return pref.getBoolean(key, defaultValue);
    }

    public void putFloat(String key, float value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key, float defaultValue) {
        return pref.getFloat(key, defaultValue);
    }
}
