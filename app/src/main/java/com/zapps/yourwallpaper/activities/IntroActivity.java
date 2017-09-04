package com.zapps.yourwallpaper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zapps.yourwallpaper.Constants;
import com.zapps.yourwallpaper.R;
import com.zapps.yourwallpaper.lib.PrefLib;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        PrefLib prefLib = PrefLib.getInstance(IntroActivity.this);
        boolean isCouple = prefLib.getBoolean(Constants.KEY_ISCOUPLE, false);
        boolean isRegister = prefLib.getBoolean(Constants.KEY_ISWAITING, false);

        Intent intent;

        if (isCouple) {
            intent = new Intent(IntroActivity.this, MainActivity.class);
        } else if (isRegister) {
            intent = new Intent(IntroActivity.this, WaitingActivity.class);
        } else {
            intent = new Intent(IntroActivity.this, RegisterActivity.class);
        }

        startActivity(intent);
    }
}
