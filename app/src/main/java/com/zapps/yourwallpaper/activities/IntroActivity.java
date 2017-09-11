package com.zapps.yourwallpaper.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zapps.yourwallpaper.Constants;
import com.zapps.yourwallpaper.R;
import com.zapps.yourwallpaper.lib.ActivityUtil;
import com.zapps.yourwallpaper.lib.PrefLib;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        PrefLib prefLib = PrefLib.getInstance(IntroActivity.this);
        boolean isCouple = prefLib.getBoolean(Constants.KEY_ISCOUPLE, false);
        boolean isRegister = prefLib.getBoolean(Constants.KEY_ISWAITING, false);

        Class targetActivity;

        if (isCouple) {
            targetActivity = MainActivity.class;
        } else if (isRegister) {
            targetActivity = WaitingActivity.class;
        } else {
            targetActivity = RegisterActivity.class;
        }

        ActivityUtil.newActivity(IntroActivity.this, targetActivity);
        // TODO: 2017. 9. 11. startactivity 를 모아놓아도 좋은방법
    }



}
