package com.zapps.yourwallpaper;

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

public class DataListenService extends Service {

    String userPhone;
    String partnerPhone;
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

        Log.d("datalisten", "service stated");
        userPhone = intent.getStringExtra(getString(R.string.key_userPhone));
        Log.d("datalisten", userPhone);
        partnerPhone = intent.getStringExtra(getString(R.string.key_partnerPhone));

        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users");
        userReference
                .orderByChild(getString(R.string.key_userPhone))
                .equalTo(userPhone)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        User user = dataSnapshot.getValue(User.class);
                        Log.d("service", "child added");
                        Log.d("service", user.getNickname());
                        if (user.isCouple) {
                            userReference.child(user.getMateKey()).child("isCouple").setValue(true);
                            userReference.child(user.getMateKey()).child("mateKey").setValue(dataSnapshot
                                    .getKey());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        User user = dataSnapshot.getValue(User.class);
                        Log.d("service", "child changed");
                        Log.d("service", user.getNickname());
                        if (!user.isCouple) {
                            userReference.child(user.getMateKey()).child("isCouple").setValue(true);
                            userReference.child(user.getMateKey()).child("mateKey").setValue(dataSnapshot
                                    .getKey());
                        }

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
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
