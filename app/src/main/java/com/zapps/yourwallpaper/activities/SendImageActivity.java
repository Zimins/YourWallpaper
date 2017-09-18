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
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.zapps.yourwallpaper.Constants;
import com.zapps.yourwallpaper.R;
import com.zapps.yourwallpaper.lib.PrefLib;

import java.io.ByteArrayOutputStream;

public class SendImageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_READ_STORAGE = 100;
    private static final int REQUEST_LOAD_IMAGE = 200;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private StorageReference reference = storage.getReference();
    private DatabaseReference dbReference = database.getReference();

    private ImageView selectedImage;
    private Button uploadButton;

    private PrefLib prefLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        prefLib = PrefLib.getInstance(SendImageActivity.this);

        selectedImage = findViewById(R.id.iv_selected_image);
        uploadButton = findViewById(R.id.btn_upload);
        uploadButton.setOnClickListener(this);

        loadImageFromGallery();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btn_upload) {
            dbReference.child("users").orderByKey()
                    .equalTo(prefLib.getString(Constants.KEY_USERID, ""))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            byte[] imageData = getDataFromImageView(selectedImage);

                            String mateKey = dataSnapshot
                                    .child(prefLib.getString(Constants.KEY_USERID, ""))
                                    .child("mateKey").getValue().toString();

                            uploadImageToKey(imageData, mateKey);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void uploadImageToKey(byte[] imageData, String mateKey) {

        // TODO: 2017. 9. 12. configure file name 무엇으로 정해야 다수의 사용자가 편하게 ?
        // TODO: 2017. 9. 18. 히스토리를 서버에 유지 할지 말지
        // TODO: 2017. 9. 12. configure file directory

        String filename = "test.jpg";

        Log.d("sendimage to", mateKey);

        StorageReference imageReference = reference.child("image/" + filename);

        final DatabaseReference partnerReference = dbReference.child("users").child(mateKey);

        UploadTask uploadTask = imageReference.putBytes(imageData);
        // TODO: 2017. 9. 18. 업로드 로딩화면이 있으면 ?
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                // todo 업로드 실패 메시지 제공하기
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type,
                // and download URL.

                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                partnerReference.child("url").setValue(downloadUrl.toString());

                // TODO: 2017. 9. 18. to string resource
                Toast.makeText(SendImageActivity.this, "upload done", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private byte[] getDataFromImageView(ImageView selectedImage) {

        selectedImage.setDrawingCacheEnabled(true);
        selectedImage.buildDrawingCache();

        Bitmap bitmap = selectedImage.getDrawingCache();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // TODO: 2017. 9. 18. request 여러개일때 상황 생각하기
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
