package com.zapps.yourwallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class WaitingActivity extends AppCompatActivity {


    Button tourButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        Intent intent = new Intent(WaitingActivity.this, DataListenService.class);
        intent.putExtra("myNumber", "01000000000");
        intent.putExtra("partnerNumber", "01012341234");
        startService(intent);

        tourButton = (Button) findViewById(R.id.button_tour);
        tourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WaitingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
