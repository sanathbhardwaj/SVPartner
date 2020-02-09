package com.sanathbhardwaj.svpartner.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanathbhardwaj.svpartner.R;
import com.sanathbhardwaj.svpartner.TimePickerFragment;

import java.util.Calendar;

public class DisplayName extends AppCompatActivity implements TimePickerFragment.TimePickerListener {

    ImageView back;
    BottomSheetDialog bottomSheetDialog;
    RelativeLayout shop_name_rl, shop_slogan_rl, shop_timing_rl, open_time, close_time;
    DatabaseReference mRef;
    TextView shop_name, shop_slogan, shop_timing, title, shop_days, open_time_tv, close_time_tv;
    EditText edit_txt;
    Button button, save;

    ToggleButton tD;
    ToggleButton tL;
    ToggleButton tM;
    ToggleButton tMi;
    ToggleButton tJ;
    ToggleButton tV;
    ToggleButton tS;
    String status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_name);


        back = findViewById(R.id.back);
        bottomSheetDialog = new BottomSheetDialog(this);
        shop_name = findViewById(R.id.shop_name);
        shop_slogan = findViewById(R.id.shop_slogan);
        shop_timing = findViewById(R.id.shop_timing);
        shop_days = findViewById(R.id.shop_days);


        shop_name_rl = findViewById(R.id.shop_name_rl);
        shop_slogan_rl = findViewById(R.id.shop_slogan_rl);
        shop_timing_rl = findViewById(R.id.shop_timing_rl);
        save = findViewById(R.id.done);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DisplayName.this, "All data saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("partner");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataSnapshot.getChildren();
                shop_name.setText(dataSnapshot.child("shop name").getValue().toString());
                shop_slogan.setText(dataSnapshot.child("slogan").getValue().toString());
//                shop_timing.setText(dataSnapshot.child("time").child("Locality").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef.child("Days_Active").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String arr[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                String days = "";

                for (int i = 0; i<arr.length; i++){

                    if (dataSnapshot.child(arr[i]).getValue().equals(true)){

                        days = days+arr[i]+", ";

                        shop_days.setText(days);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mRef.child("Time_Active").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String open_time = null, close_time = null;

                System.out.println(dataSnapshot.getValue().toString());

                int open_hour = Integer.parseInt(dataSnapshot.child("open").child("hour").getValue().toString());
                int open_minute = Integer.parseInt(dataSnapshot.child("open").child("minute").getValue().toString());
                int close_hour = Integer.parseInt(dataSnapshot.child("close").child("hour").getValue().toString());
                int close_minute = Integer.parseInt(dataSnapshot.child("close").child("minute").getValue().toString());

                if (open_hour>12){
                    open_hour = open_hour-12;

                    open_time = open_hour+":"+String.format("%02d", open_minute)+" pm";

                }
                else  {
                    open_time = open_hour+":"+String.format("%02d", open_minute)+" am";

                }

                if (close_hour>12){
                    close_hour = close_hour-12;

                    close_time = close_hour+":"+String.format("%02d", close_minute)+" pm";


                }
                else{
                    close_time = close_hour+":"+String.format("%02d", close_minute)+" am";
                }

                String s = open_time+" - "+close_time;

                shop_timing.setText(s);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        shop_name_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.setContentView(R.layout.edit);
                title = bottomSheetDialog.findViewById(R.id.title);
                edit_txt = bottomSheetDialog.findViewById(R.id.edit_txt);
                button = bottomSheetDialog.findViewById(R.id.button);
                edit_txt.setText(shop_name.getText());
                bottomSheetDialog.show();
                title.setText("Edit Shop Name");

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mRef.child("shop name").setValue(edit_txt.getText().toString());
                        Toast.makeText(DisplayName.this, "Shop Name Changed Successfully", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.setCancelable(true);


            }
        });

        shop_slogan_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.setContentView(R.layout.edit);
                title = bottomSheetDialog.findViewById(R.id.title);
                edit_txt = bottomSheetDialog.findViewById(R.id.edit_txt);
                button = bottomSheetDialog.findViewById(R.id.button);
                edit_txt.setText(shop_slogan.getText());
                bottomSheetDialog.show();
                title.setText("Edit Shop Slogan");

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mRef.child("slogan").setValue(edit_txt.getText().toString());
                        Toast.makeText(DisplayName.this, "Shop Slogan Changed Successfully", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.setCancelable(true);


            }
        });

        shop_timing_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] days = {""};

                bottomSheetDialog.setContentView(R.layout.shop_timing);
                bottomSheetDialog.show();

                tD = bottomSheetDialog.findViewById(R.id.tD);
                tL = bottomSheetDialog.findViewById(R.id.tL);
                tM = bottomSheetDialog.findViewById(R.id.tM);
                tMi = bottomSheetDialog.findViewById(R.id.tMi);
                tJ = bottomSheetDialog.findViewById(R.id.tJ);
                tV = bottomSheetDialog.findViewById(R.id.tV);
                tS = bottomSheetDialog.findViewById(R.id.tS);

                open_time = bottomSheetDialog.findViewById(R.id.open_time);
                close_time = bottomSheetDialog.findViewById(R.id.close_time);
                save = bottomSheetDialog.findViewById(R.id.save);

                open_time_tv = bottomSheetDialog.findViewById(R.id.open_time_tv);
                close_time_tv = bottomSheetDialog.findViewById(R.id.close_time_tv);


                open_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DialogFragment timePickerFragment = new TimePickerFragment();
                        timePickerFragment.setCancelable(false);
                        timePickerFragment.show(getSupportFragmentManager(), "");
                        status = "open";

                    }
                });


                close_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DialogFragment timePickerFragment = new TimePickerFragment();
                        timePickerFragment.setCancelable(false);
                        timePickerFragment.show(getSupportFragmentManager(), "");
                        status = "close";

                    }
                });


                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(tD.isChecked()){
                            days[0] +="Mon, ";
                        }
                        if(tL.isChecked()){
                            days[0] +="Tue, ";
                        }
                        if(tM.isChecked()){
                            days[0] +="Wed, ";
                        }
                        if(tMi.isChecked()){
                            days[0] +="Thu, ";
                        }
                        if(tJ.isChecked()){
                            days[0] +="Fri, ";
                        }
                        if(tV.isChecked()){
                            days[0] +="Sat, ";
                        }
                        if(tS.isChecked()){
                            days[0] +="Sun";
                        }

                        mRef.child("Days_Active").child("Mon").setValue(tD.isChecked());
                        mRef.child("Days_Active").child("Tue").setValue(tL.isChecked());
                        mRef.child("Days_Active").child("Wed").setValue(tM.isChecked());
                        mRef.child("Days_Active").child("Thu").setValue(tMi.isChecked());
                        mRef.child("Days_Active").child("Fri").setValue(tJ.isChecked());
                        mRef.child("Days_Active").child("Sat").setValue(tV.isChecked());
                        mRef.child("Days_Active").child("Sun").setValue(tS.isChecked());

                        bottomSheetDialog.dismiss();

                    }
                });

            }
        });

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int Hour, int Minute) {

        if (status.equals("open")){

            mRef.child("Time_Active").child("open").child("hour").setValue(String.valueOf(Hour));
            mRef.child("Time_Active").child("open").child("minute").setValue(String.format("%02d",Minute));

            String open_time;


            if (Hour>12){
                Hour = Hour-12;

                open_time = Hour+":"+String.format("%02d", Minute)+" pm";

            }
            else  {
                open_time = Hour+":"+String.format("%02d", Minute)+" am";

            }




            open_time_tv.setText(open_time);


        }
        if (status.equals("close")){

            mRef.child("Time_Active").child("close").child("hour").setValue(String.valueOf(Hour));
            mRef.child("Time_Active").child("close").child("minute").setValue(String.format("%02d",Minute));

            String close_time;

            if (Hour>12){
                Hour = Hour-12;

                close_time = Hour+":"+String.format("%02d", Minute)+" pm";
            }
            else{
                close_time = Hour+":"+String.format("%02d", Minute)+" am";
            }

            close_time_tv.setText(close_time);


        }

    }
}
