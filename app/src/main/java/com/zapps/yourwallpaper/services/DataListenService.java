package com.zapps.yourwallpaper.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zapps.yourwallpaper.Constants;
import com.zapps.yourwallpaper.lib.PrefLib;
import com.zapps.yourwallpaper.vo.User;

public class DataListenService extends Service {

    String userPhone;
    String partnerPhone;
    String userKey;
    String mateKey;
    PrefLib prefLib;

    public DataListenService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        prefLib = PrefLib.getInstance(DataListenService.this);
        prefLib.putBoolean(Constants.KEY_ISWAITING, true);

        userKey = prefLib.getString(Constants.KEY_USERID, "");
        userPhone = intent.getStringExtra(Constants.KEY_PHONENUMBER);
        partnerPhone = intent.getStringExtra(Constants.KEY_PARTNERNUMBER);
        //문자열 상수 인터페이스로 전환


        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        Log.d("userkeyinservice", userKey);

        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updatePartner(dataSnapshot, userRef);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return START_REDELIVER_INTENT;
    }

    private void updatePartner(DataSnapshot dataSnapshot, DatabaseReference userRef) {
        Log.d("valueevent", userKey);
        User user = dataSnapshot.getValue(User.class);

        String myKey = dataSnapshot.getKey();
        Log.d("chiledchanged", myKey);

        if (user.getMateKey() != null) {
            //add mate key to my mate
            userRef.child(user.getMateKey()).child("mateKey").setValue(myKey);
            userRef.child(user.getMateKey()).child("isCouple").setValue(true);

            prefLib.putBoolean(Constants.KEY_ISCOUPLE, true);

            Log.d("matekey", user.getMateKey());
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        prefLib.putBoolean(Constants.KEY_ISWAITING, false);
    }
}
