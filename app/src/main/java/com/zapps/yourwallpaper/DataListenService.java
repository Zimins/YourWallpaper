package com.zapps.yourwallpaper;

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

public class DataListenService extends Service {

    String myNumber;
    String partnerNumber;
    String mateKey;

    public DataListenService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("datalisten", "service stated");
        myNumber = intent.getStringExtra("userPhone");
        Log.d("datalisten", myNumber);
        partnerNumber = intent.getStringExtra("partnerPhone");

        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users");
        userReference
                .orderByChild("userPhone")
                .equalTo(myNumber)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        User user = dataSnapshot.getValue(User.class);
                        Log.d("service", "child added");
                        if (user.isCouple) {
                            userReference.child(user.getMateKey()).child("isCouple").setValue(true);
                            userReference.child(user.getMateKey()).child("mateKey").setValue(dataSnapshot
                                    .getKey());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Log.d("service", "child changed");

                        User user = dataSnapshot.getValue(User.class);
                        userReference.child(user.getMateKey()).child("isCouple").setValue(true);

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
        Log.d("datalisten", "end");
    }
}
