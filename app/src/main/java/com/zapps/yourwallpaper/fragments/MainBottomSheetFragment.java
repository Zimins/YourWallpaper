package com.zapps.yourwallpaper.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.theartofdev.edmodo.cropper.CropImage;
import com.zapps.yourwallpaper.R;
import com.zapps.yourwallpaper.activities.SendImageActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Zimincom on 2017. 9. 22..
 */

public class MainBottomSheetFragment extends BottomSheetDialogFragment {

    private static final int REQUEST_READ_STORAGE = 100;
    private static final int REQUEST_LOAD_IMAGE = 200;

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

        Log.d("bottom fragment", "onactivity result");

        if (requestCode == REQUEST_LOAD_IMAGE) {

            if (resultCode == RESULT_OK) {

                Uri selectedImageUri = data.getData();
                CropImage.activity(selectedImageUri)
                        .setAspectRatio(2,4)
                        .start(getActivity());

            }

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                if (result.isSuccessful()) {

                    Uri cropedImageUri = result.getUri();

                    Intent sendImageIntent = new Intent(getContext(), SendImageActivity
                            .class);
                    sendImageIntent.putExtra("cropedImageUri", cropedImageUri);
                    startActivity(sendImageIntent);

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Log.d("crop error", error.getMessage());
            }
        }
    }
}
