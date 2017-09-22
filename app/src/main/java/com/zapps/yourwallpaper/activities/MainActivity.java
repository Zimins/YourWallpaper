package com.zapps.yourwallpaper.activities;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.theartofdev.edmodo.cropper.CropImage;
import com.zapps.yourwallpaper.HistoryAdapter;
import com.zapps.yourwallpaper.HistoryItem;
import com.zapps.yourwallpaper.R;
import com.zapps.yourwallpaper.fragments.MainBottomSheetFragment;
import com.zapps.yourwallpaper.services.NewPictureService;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final int REQUEST_READ_STORAGE = 100;
    private static final int REQUEST_LOAD_IMAGE = 200;
    // TODO: 2017. 9. 21. 권한 앱 시작시에 받기

    @BindView(R.id.recycler_history) RecyclerView recyclerView;
    @BindView(R.id.btn_new_wallpaper) Button newWallpaperButton;
    @BindView(R.id.iv_my_wallpaper) ImageView myWallpaperImage;
    @BindView(R.id.rl_bottom_sheet) RelativeLayout bottomSheet;

    HistoryAdapter adapter;
    BottomSheetBehavior bottomBehavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        Intent intent = new Intent(MainActivity.this, NewPictureService.class);
        startService(intent);

        adapter = new HistoryAdapter(MainActivity.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        WallpaperManager wallpaperManager = (WallpaperManager) getSystemService(WALLPAPER_SERVICE);

        Drawable myWallpaper = wallpaperManager.getDrawable();

        myWallpaperImage.setImageDrawable(myWallpaper);

        bottomBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }



    @Override
    protected void onResume() {
        super.onResume();
        // TODO: 2017. 9. 21. 새로 보낼때 갱신하기 (지금 2배씩 늘어남)
        loadImagesFromDisk(adapter);
    }

    private void loadImagesFromDisk(HistoryAdapter adapter) {
        String historyFilesDirName = Environment.getExternalStorageDirectory() + "/" + "Pictures"
                + "/" + "wallhistory";
        Log.d("path" , historyFilesDirName);

        adapter.clearItems();

        File historyDir = new File(historyFilesDirName);
        File[] historyImages = historyDir.listFiles();

        for (File image : historyImages) {
            adapter.addItem(new HistoryItem(image, image.getName()));
        }
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
        Log.d("main", "show dialog");
        MainBottomSheetFragment bottomSheet = new MainBottomSheetFragment();
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }


    private void showImageGallery() {
        Intent intent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, REQUEST_LOAD_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOAD_IMAGE) {

            if (resultCode == RESULT_OK) {

                Uri selectedImageUri = data.getData();
                CropImage.activity(selectedImageUri)
                        .setAspectRatio(2,4)
                        .start(MainActivity.this);

            }

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                if (result.isSuccessful()) {

                    Uri cropedImageUri = result.getUri();

                    Intent sendImageIntent = new Intent(MainActivity.this, SendImageActivity
                            .class);
                    sendImageIntent.putExtra("imageUri", cropedImageUri);
                    startActivity(sendImageIntent);

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Log.d("crop error", error.getMessage());
            }
        } else if (requestCode == MainBottomSheetFragment.REQUEST_IMAGE_CAPTURE && resultCode ==
                RESULT_OK) {

            String historyFilesDirName = Environment.getExternalStorageDirectory() + "/" + "Pictures"
                    + "/" + "wallhistory";

            //String capturedDir = get
           // Log.d("path" , capturedDir);

            Uri capturedImageUri = (Uri) data.getExtras().get("data");

            Intent sendImageIntent = new Intent(MainActivity.this, SendImageActivity
                    .class);
//            sendImageIntent.putExtra("imageUri", capturedImageUri);
//            startActivity(sendImageIntent);
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }
}
