package com.zapps.yourwallpaper.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zapps.yourwallpaper.R;

import java.io.ByteArrayOutputStream;

public class SendImageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_READ_STORAGE = 100;
    private static final int REQUEST_LOAD_IMAGE = 200;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference reference = storage.getReference();

    private ImageView selectedImage;
    private Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        selectedImage = findViewById(R.id.iv_selected_image);
        uploadButton = findViewById(R.id.btn_upload);
        uploadButton.setOnClickListener(this);

        loadImageFromGallery();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_upload) {
            // TODO: 2017. 9. 12. image upload to server(ok) 
            // TODO: 2017. 9. 12. update db info of partner 
            // TODO: 2017. 9. 12. when db update, download image 
            // TODO: 2017. 9. 12. configure file name  
            String filename = "test.jpg";
            // TODO: 2017. 9. 12. configure file directory 
            StorageReference imageReference = reference.child("image/" + filename);
            selectedImage.setDrawingCacheEnabled(true);
            selectedImage.buildDrawingCache();
            Bitmap bitmap = selectedImage.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageReference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    // TODO: 2017. 9. 12. add this url to partner
                    Toast.makeText(SendImageActivity.this, "upload done", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    private void loadImageFromGallery() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_LOAD_IMAGE || resultCode != RESULT_OK) return;

        Uri selectedImageUri = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver()
                .query(selectedImageUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();


        selectedImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        selectedImage.setScaleType(ImageView.ScaleType.MATRIX);
        selectedImage.bringToFront();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
