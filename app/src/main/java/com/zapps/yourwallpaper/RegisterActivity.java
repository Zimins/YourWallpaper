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
                startActivity(intent);
            }
        });
    }

    private void writeNewUser(String nickname, String phoneNumber, String partnerNumber) {
        Log.i("write", "new user");
        user = new User(phoneNumber, partnerNumber);
        reference.child(nickname).setValue(user);
    }
    private void updateToCouple(String phoneNumber) {
        Log.i("update db", "flag on");
        reference.orderByChild("phone").equalTo("01012341234").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                partner = dataSnapshot.getValue(User.class);
                user.setIsCouple(true);
                reference.child(partner.getNickname()).setValue(user);
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
