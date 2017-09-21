package com.zapps.yourwallpaper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.zapps.yourwallpaper.HistoryAdapter;
import com.zapps.yourwallpaper.HistoryItem;
import com.zapps.yourwallpaper.R;
import com.zapps.yourwallpaper.services.NewPictureService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    @BindView(R.id.recycler_history) RecyclerView recyclerView;
    @BindView(R.id.btn_new_wallpaper) Button newWallpaperButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //todo 사진 찍어서 입력도 지원
        Intent intent = new Intent(MainActivity.this, NewPictureService.class);
        startService(intent);

        HistoryAdapter adapter = new HistoryAdapter();

        adapter.addItem(new HistoryItem(R.drawable.empty_image, "hello"));
        adapter.addItem(new HistoryItem(R.drawable.empty_image, "hello"));
        adapter.addItem(new HistoryItem(R.drawable.empty_image, "hello"));
        adapter.addItem(new HistoryItem(R.drawable.empty_image, "hello"));
        adapter.addItem(new HistoryItem(R.drawable.empty_image, "hello"));
        adapter.addItem(new HistoryItem(R.drawable.empty_image, "hello"));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
    }

    @OnClick(R.id.btn_new_wallpaper)
    public void showSelectDialog(View v) {
        Intent intent = new Intent(MainActivity.this, SendImageActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
