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

public class CoupleDetectService extends Service implements ChildEventListener{

    String userPhone;
    String partnerPhone;
    String userKey;
    PrefLib prefLib;

    // TODO: 2017. 9. 18. 전체 유저 리스트를 참조하고 있는데 버그 고려하기
    DatabaseReference userListRef = FirebaseDatabase.getInstance().getReference("users/" +
            userKey);

    public CoupleDetectService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        prefLib = PrefLib.getInstance(CoupleDetectService.this);
        prefLib.putBoolean(Constants.KEY_ISWAITING, true);

        userKey = prefLib.getString(Constants.KEY_USERID, "");
        userPhone = intent.getStringExtra(Constants.KEY_PHONENUMBER);
        partnerPhone = intent.getStringExtra(Constants.KEY_PARTNERNUMBER);

        userListRef.addChildEventListener(this);

        return START_REDELIVER_INTENT;
    }

    private void updatePartner(DataSnapshot dataSnapshot, DatabaseReference userRef) {
        // 나의 matekey
        Log.d("valueevent", userKey);
        User user = dataSnapshot.getValue(User.class);

        String myKey = dataSnapshot.getKey();
        Log.d("chiledchanged", myKey);

        if (user.getMateKey() != null) {
            //add mate key to my mate
            userRef.child(user.getMateKey()).child("mateKey").setValue(myKey);
            userRef.child(user.getMateKey()).child("isCouple").setValue(true);

            prefLib.putString(Constants.KEY_PARTNER, user.getMateKey());
            prefLib.putBoolean(Constants.KEY_ISCOUPLE, true);

            Log.d("matekey on datalisten", user.getMateKey());
            userListRef.removeEventListener(this);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        prefLib.putBoolean(Constants.KEY_ISWAITING, false);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        updatePartner(dataSnapshot, userListRef);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        updatePartner(dataSnapshot, userListRef);

        // 유지되는 리스너 삭제
        userListRef.removeEventListener(this);
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
}
