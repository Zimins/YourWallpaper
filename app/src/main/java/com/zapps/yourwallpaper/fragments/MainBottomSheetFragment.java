package com.zapps.yourwallpaper.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.zapps.yourwallpaper.R;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Zimincom on 2017. 9. 22..
 */

public class MainBottomSheetFragment extends BottomSheetDialogFragment {

    private static final int REQUEST_READ_STORAGE = 100;
    private static final int REQUEST_LOAD_IMAGE = 200;
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.fragment_main_bottom_sheet, null);
        dialog.setContentView(view);

        LinearLayout showGalleryButton = view.findViewById(R.id.layout_gallery);
        LinearLayout showCameraButton = view.findViewById(R.id.layout_camera);

        showGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                selectFromGallery();
            }
        });
        
        showCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                loadImageFromCamera();
            }
        });
    }

    private void loadImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("exception", "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri takenPhotoUri = FileProvider.getUriForFile(getContext(),
                        "com.zapps.yourwallpaper.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, takenPhotoUri);
                getActivity().startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    public void selectFromGallery() {

        if (ContextCompat
                .checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);

        } else {
            showImageGallery();
        }
    }

    private void showImageGallery() {
        Intent intent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        getActivity().startActivityForResult(intent, REQUEST_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
