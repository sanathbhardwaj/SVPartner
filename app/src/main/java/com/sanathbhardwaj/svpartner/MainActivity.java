package com.sanathbhardwaj.svpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanathbhardwaj.svpartner.menu.AddProduct;
import com.sanathbhardwaj.svpartner.menu.ChangeBanner;
import com.sanathbhardwaj.svpartner.menu.Credits;
import com.sanathbhardwaj.svpartner.menu.DisplayName;
import com.sanathbhardwaj.svpartner.menu.ProfileActivity;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    RelativeLayout credits, add_product, account, order, banner, display_name, all_products, analytics;
    TextView orders, active, location;
    ImageView active_img;
    Dialog myDialog, order_state;
    Switch list_toggle;
    String uid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_toggle = findViewById(R.id.list_toggle);
        list_toggle.setOnCheckedChangeListener(this);
        myDialog = new Dialog(this);
        order_state = new Dialog(this);
        orders = findViewById(R.id.orders);
        order = findViewById(R.id.order);
        active = findViewById(R.id.status);
        active_img = findViewById(R.id.active_img);
        active.setText("Inactive");
        active_img.setVisibility(View.INVISIBLE);
        add_product = findViewById(R.id.add_product);
        credits = findViewById(R.id.credits);
        display_name = findViewById(R.id.display_name);
        account = findViewById(R.id.account);
        all_products = findViewById(R.id.all_products);
        analytics = findViewById(R.id.analytics);
        myDialog.setContentView(R.layout.not_registered_popup);
        order_state.setContentView(R.layout.new_order);

        location = findViewById(R.id.location);


        banner = findViewById(R.id.banner);

        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            Intent intent = new Intent(MainActivity.this, PhoneEntry.class);
            startActivity(intent);
            finish();
            uid = "";
        }

        else {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(uid).child("partner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("LatLog")&&!(MainActivity.this).isFinishing()){
                    showPopUp();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Credits.class);
                startActivity(intent);

            }
        });

        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChangeBanner.class);
                startActivity(intent);


            }
        });

        display_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, DisplayName.class);
                startActivity(intent);

            }
        });

        all_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Credits.class);
                startActivity(intent);

            }
        });

        analytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Credits.class);
                startActivity(intent);



            }
        });

        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String services = getIntent().getStringExtra("services");
                Intent intent = new Intent(MainActivity.this, AddProduct.class);
                intent.putExtra("services", services);
                startActivity(intent);
            }
        });
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);

            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Credits.class);
                startActivity(intent);
            }
        });

        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChangeBanner.class);
                startActivity(intent);
            }
        });



        DatabaseReference mOrdersRef;
        mOrdersRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("partner").child("orders");

        mOrdersRef.child("orderID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue()!=null) {
                    String s = String.valueOf(dataSnapshot.getChildrenCount());
                    String order = dataSnapshot.getChildren().toString();
                    System.out.println(order);
                    orders.setText(s);
                }
                else {
                    orders.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    public void showPopUp(){
        Button register;
        register = myDialog.findViewById(R.id.register);
        myDialog.show();
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(false);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, CompleteForm.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });
    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        DatabaseReference m = FirebaseDatabase.getInstance().getReference("users");

        m.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                location.setText(dataSnapshot.child("Address").child("City").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String City = location.getText().toString();


        if(isChecked) {
            DatabaseReference mDatabase, getmDatabase;



            getmDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("category");

            getmDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String s = dataSnapshot.getValue().toString();

                    DatabaseReference productDatabase = FirebaseDatabase.getInstance().getReference("shops").child("category").child(s).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    productDatabase.child("status_location").setValue("Active_"+City);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mDatabase = FirebaseDatabase.getInstance().getReference("users");
            list_toggle.setText("Online");
            active.setText("Active");
            active_img.setVisibility(View.VISIBLE);
            mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("status_location").setValue("Active_"+City);
            //To change the text near to switch
        }
        else {
            DatabaseReference mDatabase, getmDatabase;

            getmDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("category");

            getmDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String s = dataSnapshot.getValue().toString();

                    DatabaseReference productDatabase = FirebaseDatabase.getInstance().getReference("shops").child("category").child(s).child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    productDatabase.child("status_location").setValue("InActive_"+City);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mDatabase = FirebaseDatabase.getInstance().getReference("users");
            list_toggle.setText("Offline");
            active.setText("Inactive");
            active_img.setVisibility(View.INVISIBLE);
            mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("status_location").setValue("InActive_"+City);
            //To change the text near to switch
        }
    }
}

