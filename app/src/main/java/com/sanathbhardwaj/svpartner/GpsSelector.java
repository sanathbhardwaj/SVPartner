package com.sanathbhardwaj.svpartner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class GpsSelector extends AppCompatActivity {

    Button location_choose;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_selector);
        location_choose = findViewById(R.id.location_choose);



        mDatabase = FirebaseDatabase.getInstance().getReference();

        location_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withActivity(GpsSelector.this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                String services = getIntent().getStringExtra("services");

                                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user").child("Name").setValue(null);
                                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user").child("Address").setValue(null);
                                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user").child("LatLog").setValue(null);
                                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user").child("phone number").setValue(null);
                                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user").child("Orders Active").setValue(null);
                                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user").child("Orders Cancelled").setValue(null);
                                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user").child("All Orders").setValue(null);
                                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("user").child("Total Amount Spent").setValue(null);
                                Intent intent = new Intent(GpsSelector.this, CurrentLocation.class);
                                String s = intent.getStringExtra("address");
                                intent.putExtra("address",s);
                                intent.putExtra("services", services);
                                startActivity(intent);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                                if (response.isPermanentlyDenied()) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(GpsSelector.this);
                                    builder.setTitle("Permission is Required")
                                            .setMessage("Permission is not granted. You need to go to settings and allow the required permission")
                                            .setNegativeButton("Cancel", null)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent();
                                                    intent.setData(Uri.fromParts("package", getPackageName(), null));

                                                }
                                            })
                                            .show();
                                }

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        })
                        .check();

            }
        });


    }
}
