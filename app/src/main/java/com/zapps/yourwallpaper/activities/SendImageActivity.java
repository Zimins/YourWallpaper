package com.zapps.yourwallpaper.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zapps.yourwallpaper.Constants;
import com.zapps.yourwallpaper.R;
import com.zapps.yourwallpaper.lib.PrefLib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SendImageActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_READ_STORAGE = 100;
    private static final int REQUEST_LOAD_IMAGE = 200;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private StorageReference reference = storage.getReference();
    private DatabaseReference dbReference = database.getReference();

    private ImageView selectedImage;
    private BottomNavigationView bottomNavigation;

    private PrefLib prefLib;

    private Intent intent;

    // TODO: 2017. 9. 21. image 보낼때 progressbar
    // TODO: 2017. 9. 21. 보내고 나서 액티비티 종료하기  혹은 누르자 마자 종료하기 (ex: notibar 사용)
    // TODO: 2017. 9. 21. 서버에 올라갈 파일 이름 정하기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();

        prefLib = PrefLib.getInstance(SendImageActivity.this);

        selectedImage = findViewById(R.id.iv_selected_image);
        bottomNavigation = findViewById(R.id.bottomNavigationView);

        bottomNavigation.setOnNavigationItemSelectedListener(this);

        if (intent.hasExtra("imageUri")) {
            Log.d("sendimageact", "setImageUri");
            Picasso.with(this).load((Uri)intent.getExtras().get("imageUri")).into(selectedImage);
        } else if (intent.hasExtra("imageBitmap")) {
            selectedImage.setImageBitmap((Bitmap) intent.getExtras().get("imageBitmap"));
        }

        //loadImageFromGallery();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {

        } else if (id == R.id.action_choose) {
            loadImageFromGallery();
        } else if (id == R.id.action_upload) {
            dbReference.child("users").orderByKey()
                    .equalTo(prefLib.getString(Constants.KEY_USERID, ""))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            byte[] imageData = getDataFromImageView(selectedImage);

                            if(!prefLib.getBoolean(Constants.KEY_ISCOUPLE, false)) {
                                Toast.makeText(SendImageActivity.this, "you are not couple!",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String mateKey = dataSnapshot
                                    .child(prefLib.getString(Constants.KEY_USERID, ""))
                                    .child("mateKey").getValue().toString();

                            uploadImageToKey(imageData, mateKey);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            return true;
        }
        return false;
    }

    private void uploadImageToKey(final byte[] imageData, String mateKey) {

        // TODO: 2017. 9. 12. configure file name 무엇으로 정해야 다수의 사용자가 편하게 ?
        // TODO: 2017. 9. 18. 히스토리를 서버에 유지 할지 말지

        String filename = "test.jpg";

        Log.d("sendimage to", mateKey);

        StorageReference imageReference = reference.child("image/" + filename);

        final DatabaseReference partnerReference = dbReference.child("users").child(mateKey);

        UploadTask uploadTask = imageReference.putBytes(imageData);
        // TODO: 2017. 9. 18. 업로드 로딩화면이 있으면 ?
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(SendImageActivity.this, "Upload Fail!", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type,
                // and download URL.

                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                partnerReference.child("url").setValue(downloadUrl.toString());

                Toast.makeText(SendImageActivity.this, "upload done", Toast.LENGTH_SHORT)
                        .show();

                int fileNum = prefLib.getInt(Constants.KEY_WALLNUMBER, 0);

                if (isExternalStorageWritable()) {

                    File file =
                            new File(getAlbumStorageDir("/wallhistory"),
                                    "wall" + fileNum + ".jpeg");
                    prefLib.putInt(Constants.KEY_WALLNUMBER, ++fileNum);

                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        out.write(imageData);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }

    private File getAlbumStorageDir(String albumName) {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_PICTURES), albumName);

        if (!file.mkdirs()) {
            Log.e("file error", "directory not created");
        }

        return file;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }

        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }

        return false;
    }

    private byte[] getDataFromImageView(ImageView selectedImage) {

        Bitmap bitmap = ((BitmapDrawable) selectedImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        return baos.toByteArray();
    }


    private void loadImageFromGallery() {

        // TODO: 2017. 9. 18. 권한체크 부분 고려.(저장공간)
        if (ContextCompat
                .checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);

        } else {
            showImageGallery();
        }
    }

    private void showImageGallery() {
        Intent intent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, REQUEST_LOAD_IMAGE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOAD_IMAGE) {

            if (resultCode == RESULT_OK) {

                Uri selectedImageUri = data.getData();
                CropImage.activity(selectedImageUri)
                        .setAspectRatio(2,4)
                        .start(SendImageActivity.this);

            }

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                if (result.isSuccessful()) {

                    Uri cropedImageUri = result.getUri();

                    try {
                        selectedImage.setImageBitmap(MediaStore.Images.Media.getBitmap
                                (getContentResolver(), cropedImageUri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    selectedImage.bringToFront();

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Log.d("crop error", error.getMessage());
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode != REQUEST_READ_STORAGE) return;

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //permission granted
            showImageGallery();
        } else {
            // notify you can't use service
            Toast.makeText(SendImageActivity.this, "권한 없이 실행 불가합니다", Toast.LENGTH_SHORT).show();
            // make notify message

        }

    }
}
