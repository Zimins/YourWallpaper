package com.zapps.yourwallpaper.services;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zapps.yourwallpaper.Constants;
import com.zapps.yourwallpaper.lib.PrefLib;

import java.io.IOException;

public class NewPictureService extends Service {

    // TODO: 2017. 9. 21. file 받으면 히스토리에 저장 ? 안저장 ?
    PrefLib prefLib;

    //GC 때문에 클래스 변수로 선언
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d("bitmapLoading", "bitmap done");
            WallpaperManager manager = (WallpaperManager) getApplicationContext()
                    .getSystemService(WALLPAPER_SERVICE);
            try {
                manager.setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d("bitmapLoading", "failed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d("bitmapLoading", "onprepareLoad");
        }
    };

    public NewPictureService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        prefLib = PrefLib.getInstance(NewPictureService.this);

        String userKey = prefLib.getString(Constants.KEY_USERID, "");

        Log.d("newpictureservice", userKey);

        // TODO: 2017. 9. 18. 변수명 변경 
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users" + "/" +
                userKey );
        userReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("chiled added", dataSnapshot.toString());
                if (dataSnapshot.getKey().equals("url")) {
                   Log.d("new url", dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("chiled chaged", dataSnapshot.toString());

                // TODO: 2017. 9. 18. string resource
                Toast.makeText(getApplicationContext(), "새로운 사진", Toast.LENGTH_SHORT).show();

                if (dataSnapshot.getKey().equals("url")) {
                    String downloadUrl = dataSnapshot.getValue().toString();
                    setWallpaperBitmapFromUrl(downloadUrl);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return START_STICKY;
    }

    private void setWallpaperBitmapFromUrl(String url) {
        Log.d("newPicture setting", url );
        Picasso.with(NewPictureService.this).load(url).into(target);
    }


}
