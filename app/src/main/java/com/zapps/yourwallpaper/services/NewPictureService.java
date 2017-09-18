package com.zapps.yourwallpaper.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zapps.yourwallpaper.Constants;
import com.zapps.yourwallpaper.lib.PrefLib;

public class NewPictureService extends Service {

    PrefLib prefLib;

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

        DatabaseReference userRef2 = FirebaseDatabase.getInstance().getReference("users" + "/" +
                userKey );
        userRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("chiled added:ref2", "added");
                if (dataSnapshot.getKey().equals("url")) {
                   Log.d("new url", dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("chiled chaged:ref2", "changed");
                Toast.makeText(getApplicationContext(), "새로운 사진", Toast.LENGTH_SHORT).show();
                if (dataSnapshot.getKey().equals("url")) {

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("chiled moved:ref2", s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return START_STICKY;
    }
}
