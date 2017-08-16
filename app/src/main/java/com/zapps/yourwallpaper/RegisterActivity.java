package com.zapps.yourwallpaper;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;

    Button confirmButton;
    EditText nicknameInput;
    EditText userNumInput;
    EditText partnerNumInput;

    String nickname;
    String userNumber;
    String partnerNumber;
    String userKey;

    User user;
    User partner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        confirmButton = (Button) findViewById(R.id.button_confirm);

        nicknameInput = (EditText) findViewById(R.id.input_nickname);
        userNumInput = (EditText) findViewById(R.id.input_my_nunber);
        partnerNumInput = (EditText) findViewById(R.id.input_partner_number);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nickname = nicknameInput.getText().toString();
                userNumber = userNumInput.getText().toString();
                partnerNumber = partnerNumInput.getText().toString();

                if (nickname.equals("")) {
                    nicknameInput.setError("empty nickname!");
                    return;
                }
                // it should change to more hard detect
                if (userNumber.equals("")) {
                    userNumInput.setError("wrong number!");
                    return;
                }

                if(!PhoneNumberUtils.isGlobalPhoneNumber(userNumber)) {
                    userNumInput.setError("wrong number!");
                    return;
                }

                if (partnerNumber.equals("")) {
                    partnerNumInput.setError("wrong number!");
                    return;
                }
                writeNewUser(nickname, userNumber, partnerNumber);
                updateToCouple(partnerNumber);
                Intent intent = new Intent(RegisterActivity.this, WaitingActivity.class);
                intent.putExtra("userPhone", userNumber);
                intent.putExtra("partnerPhone", partnerNumber);
                startActivity(intent);
            }
        });
    }

    private void writeNewUser(String nickname, String phoneNumber, String partnerNumber) {
        Log.i("write", "new user");
        user = new User(nickname, phoneNumber, partnerNumber);
        DatabaseReference newUserRef = reference.push();
        newUserRef.setValue(user);
        userKey = newUserRef.getKey();
        Log.d("push key", userKey);
    }

    private void updateToCouple(String phoneNumber) {
        Log.i("update db", "flag on");
        reference
                .orderByChild("userPhone")
                .equalTo(phoneNumber)
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //찾고 해당 키의 밸류 를 변경
                partner = dataSnapshot.getValue(User.class);
                String mateKey = dataSnapshot.getKey();
                Log.d("register", mateKey);
                if (!partner.isCouple) {
                    partner.setIsCouple(true);
                    reference.child(mateKey).child("isCouple").setValue(true);
                    reference.child(mateKey).child("mateKey").setValue(userKey);
                }
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

}