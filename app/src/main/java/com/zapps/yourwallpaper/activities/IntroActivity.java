package com.zapps.yourwallpaper.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zapps.yourwallpaper.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        SharedPreferences pref = getSharedPreferences(getString(R.string.key_preference_file),
                Context.MODE_PRIVATE);
        boolean isRegister = pref.getBoolean(getString(R.string.key_isRegister),false);

        Intent intent;
        if (isRegister) {
            intent = new Intent(IntroActivity.this, WaitingActivity.class);
        } else {
            intent = new Intent(IntroActivity.this, RegisterActivity.class);
        }
        startActivity(intent);
    }
}
