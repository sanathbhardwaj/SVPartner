package com.sanathbhardwaj.svpartner.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanathbhardwaj.svpartner.CurrentLocation;
import com.sanathbhardwaj.svpartner.PhoneEntry;
import com.sanathbhardwaj.svpartner.R;

public class ProfileActivity extends AppCompatActivity {

    Button logout, button;
    TextView mobile, name, email, address, title;
    EditText edit_txt;
    DatabaseReference mRef;
    String uid;
    RelativeLayout current_address, full_name, email_address;
    BottomSheetDialog bottomSheetDialog;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        bottomSheetDialog = new BottomSheetDialog(this);
        logout = findViewById(R.id.logout);
        mobile = findViewById(R.id.mobile);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        back = findViewById(R.id.back);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mobile.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        current_address = findViewById(R.id.current_address);
        full_name = findViewById(R.id.full_name);
        email_address = findViewById(R.id.emailaddress);

        mRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("partner");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataSnapshot.getChildren();
                name.setText(dataSnapshot.child("full name").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());
                address.setText(dataSnapshot.child("Address").child("Locality").getValue().toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        full_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.setContentView(R.layout.edit);
                title = bottomSheetDialog.findViewById(R.id.title);
                edit_txt = bottomSheetDialog.findViewById(R.id.edit_txt);
                button = bottomSheetDialog.findViewById(R.id.button);
                edit_txt.setText(name.getText());
                bottomSheetDialog.show();
                title.setText("Edit Name");

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mRef.child("full name").setValue(edit_txt.getText().toString());
                        Toast.makeText(ProfileActivity.this, "Name Changed Successfully", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.setCancelable(true);

            }
        });

        email_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.setContentView(R.layout.edit);
                title = bottomSheetDialog.findViewById(R.id.title);
                edit_txt = bottomSheetDialog.findViewById(R.id.edit_txt);
                button = bottomSheetDialog.findViewById(R.id.button);
                edit_txt.setText(email.getText());
                bottomSheetDialog.show();
                title.setText("Edit Email");

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mRef.child("email").setValue(edit_txt.getText().toString());
                        Toast.makeText(ProfileActivity.this, "Email Changed Successfully", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.setCancelable(true);

            }

        });




        current_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, CurrentLocation.class);
                startActivity(intent);
            }
        });



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, PhoneEntry.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

}
