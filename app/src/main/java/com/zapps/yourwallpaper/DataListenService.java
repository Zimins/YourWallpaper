package com.zapps.yourwallpaper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataListenService extends Service {

    String myNumber;
    String partnerNumber;

    public DataListenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        // TODO: Return the communication channel to the service.
        myNumber = intent.getStringExtra("myNumber");
        partnerNumber = intent.getStringExtra("partnerNumber");

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users");
        userReference
                .orderByChild("phone")
                .equalTo("01000000000")
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                Log.d("servicemessage", user.toString());
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

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
