package com.zapps.yourwallpaper.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zapps.yourwallpaper.R;
import com.zapps.yourwallpaper.services.DataListenService;

public class WaitingActivity extends AppCompatActivity {

    Button tourButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        Intent intent = getIntent();
        Intent waitService = new Intent(WaitingActivity.this, DataListenService.class);

        //service 를 하나만 돌아가게 하려면?
        if (!isServiceRunning(DataListenService.class)) {
            //todo contants 에서 가져오기
            waitService.putExtra(getString(R.string.key_userPhone) , intent.getStringExtra
                    ("userPhone"));
            waitService.putExtra(getString(R.string.key_partnerPhone), intent.getStringExtra("partnerPhone"));
            startService(waitService);
        }

        tourButton = (Button) findViewById(R.id.button_tour);
        tourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WaitingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }

        }

        return false;
    }
}
