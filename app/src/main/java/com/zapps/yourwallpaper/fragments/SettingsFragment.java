package com.zapps.yourwallpaper.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.zapps.yourwallpaper.Constants;
import com.zapps.yourwallpaper.R;
import com.zapps.yourwallpaper.activities.IntroActivity;
import com.zapps.yourwallpaper.lib.PrefLib;

/**
 * Created by Zimincom on 2017. 8. 3..
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences
        .OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_yourwallpaper);

        //final SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        final SharedPreferences sharedPreferences = PrefLib.getInstance(getContext()).getPref();

        PreferenceScreen preferenceScreen = getPreferenceScreen();

        int count = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                Log.d("keys", preference.getKey());
                setPreferenceSummary(preference, value);
            }
        }

        Preference logoutButton = findPreference("key_logout");
        logoutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                PrefLib.getInstance(getContext()).putBoolean(Constants.KEY_ISCOUPLE, false);
                PrefLib.getInstance(getContext()).putBoolean(Constants.KEY_ISWAITING, false);
                Toast.makeText(getContext(), "logout", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), IntroActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }

    private void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) preference;
            editTextPreference.setSummary(value);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference preference = findPreference(s);

        if (preference != null) {
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(s, "현재 정보가 없습니다.");
                setPreferenceSummary(preference, value);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
