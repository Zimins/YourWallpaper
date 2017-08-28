package com.zapps.yourwallpaper.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zapps.yourwallpaper.R;
import com.zapps.yourwallpaper.vo.User;

public class DataListenService extends Service {

    String userPhone;
    String partnerPhone;
    String userKey;
    String mateKey;
    SharedPreferences pref;

    public DataListenService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        pref = getSharedPreferences(getString(R.string.key_preference_file),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(getString(R.string.key_isWaiting), true);
        editor.apply();

        userKey = pref.getString(getString(R.string.key_user), "");
        userPhone = intent.getStringExtra(getString(R.string.key_userPhone));
        partnerPhone = intent.getStringExtra(getString(R.string.key_partnerPhone));

        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        Log.d("userkeyinservice", userKey);

        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("valueevent", userKey);
                User user = dataSnapshot.getValue(User.class);

                String myKey = dataSnapshot.getKey();
                Log.d("chiledchanged", myKey);

                if (user.getMateKey() != null) {
                    //add mate key to my mate
                    userRef.child(user.getMateKey()).child("mateKey").setValue(myKey);
                    userRef.child(user.getMateKey()).child("isCouple").setValue(true);
                    //파일 데이터가 바뀌면 나의 커플 상태를 변경
                    Log.d("matekey", user.getMateKey());
                }
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(getString(R.string.key_isWaiting), false);
        editor.apply();
        Log.d("datalisten", "end");
    }
}
