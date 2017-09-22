package com.zapps.yourwallpaper.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zapps.yourwallpaper.Constants;
import com.zapps.yourwallpaper.R;
import com.zapps.yourwallpaper.lib.ActivityLib;
import com.zapps.yourwallpaper.lib.PrefLib;
import com.zapps.yourwallpaper.vo.User;

public class RegisterActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;

    Button confirmButton;
    EditText nicknameInput;
    EditText userNumInput;
    EditText partnerNumInput;

    String nickname;
    String userPhone;
    String partnerPhone;
    String userKey;

    User user;
    User partner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        confirmButton = findViewById(R.id.button_confirm);
        nicknameInput = findViewById(R.id.input_nickname);
        userNumInput = findViewById(R.id.input_my_number);
        partnerNumInput = findViewById(R.id.input_partner_number);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInputVailed()) {
                    writeNewUser(nickname, userPhone, partnerPhone);

                    updateToCouple(partnerPhone);

                    ActivityLib.getInstance()
                            .goWaitinActivity(RegisterActivity.this, userPhone, partnerPhone);
                }
            }
        });
    }


    private boolean checkInputVailed() {
        nickname = nicknameInput.getText().toString();
        userPhone = userNumInput.getText().toString();
        partnerPhone = partnerNumInput.getText().toString();

        if (nickname.equals("")) {
            nicknameInput.setError("empty nickname!");
            return false;
        }
        // it should change to more hard detect
        if (userPhone.equals("")) {
            userNumInput.setError("wrong number!");
            return false;
        }

        if (!PhoneNumberUtils.isGlobalPhoneNumber(userPhone)) {
            userNumInput.setError("wrong number!");
            return false;
        }

        if (partnerPhone.equals("")) {
            partnerNumInput.setError("wrong number!");
            return false;
        }

        return true;
    }

    private void writeNewUser(String nickname, String phoneNumber, String partnerNumber) {
        //check vaild
        // TODO: 2017. 9. 18. 번호가 맞는 번호인가?
        // TODO: 2017. 9. 18. 이미 가입한 사용자는 아닌가?
        // TODO: 2017. 9. 18. 찾는 사람이 없다면 ? 중복이라면 ?
        user = new User(nickname, phoneNumber, partnerNumber);
        DatabaseReference newUserRef = reference.push();
        newUserRef.setValue(user);
        userKey = newUserRef.getKey();
        PrefLib prefLib = PrefLib.getInstance(RegisterActivity.this);

        prefLib.putString(Constants.KEY_NICKNAME, nickname);
        prefLib.putString(Constants.KEY_USERID, userKey);
        prefLib.putString(Constants.KEY_PHONENUMBER, phoneNumber);
        prefLib.putString(Constants.KEY_PARTNERNUMBER, partnerNumber);

        prefLib.putBoolean(Constants.KEY_ISREGISTER, true);
        prefLib.putBoolean(Constants.KEY_ISCOUPLE, false);
        //string constant problem
    }

    private void updateToCouple(String partnerPhone) {
        reference
                .orderByChild("userPhone")
                .equalTo(partnerPhone)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        updatePartner(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    private void updatePartner(DataSnapshot dataSnapshot) {
        partner = dataSnapshot.getValue(User.class);
        String mateKey = dataSnapshot.getKey();
        Log.d("register", mateKey);
        if (!partner.getIsCouple()) {
            partner.setIsCouple(true);
            reference.child(mateKey).child("mateKey").setValue(userKey);
            reference.child(mateKey).child("isCouple").setValue(true);
        }
    }

}
