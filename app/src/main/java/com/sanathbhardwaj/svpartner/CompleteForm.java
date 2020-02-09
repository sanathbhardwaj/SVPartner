package com.sanathbhardwaj.svpartner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.isapanah.awesomespinner.AwesomeSpinner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class CompleteForm extends FragmentActivity implements TimePickerFragment.TimePickerListener{

    EditText full_name, shop_name, shop_slogan, email,password;
    TextView phone_number, u, shop_timing, shop_days;
    Button submit;
    private DatabaseReference mDatabase;
    RelativeLayout upload_rl;
    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri filePath;
    ImageView upload;
    BottomSheetDialog bottomSheetDialog;

    RelativeLayout open_time, close_time;
    TextView open_time_tv, close_time_tv;

    Button save;
    LinearLayout shop_timing_ll;


    Spinner my_spinner;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;

    ToggleButton tD;
    ToggleButton tL;
    ToggleButton tM;
    ToggleButton tMi;
    ToggleButton tJ;
    ToggleButton tV;
    ToggleButton tS;

    DatabaseReference mRef;
    String status = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_form);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        full_name = findViewById(R.id.full_name);
        bottomSheetDialog = new BottomSheetDialog(this);
        shop_name = findViewById(R.id.shop_name);
        shop_slogan = findViewById(R.id.shop_slogan);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone_number = findViewById(R.id.phone_number);
        submit = findViewById(R.id.submit);
        my_spinner = findViewById(R.id.shop_category);
        upload = findViewById(R.id.upload);
        u = findViewById(R.id.u);
        shop_timing = findViewById(R.id.shop_timing);
        shop_days = findViewById(R.id.shop_days);
        shop_timing_ll = findViewById(R.id.shop_timing_ll);


        final String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        phone_number.setText(phone);
        mRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("partner");

        shop_timing_ll.setOnClickListener(new View.OnClickListener() {
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

                        mRef.child("Days_Active").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String arr[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                                String days = "";

                                for (int i = 0; i<arr.length; i++){

                                    if (dataSnapshot.getValue()!=null && dataSnapshot.child(arr[i]).getValue().equals(true)){

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

                                if (dataSnapshot.getValue()!=null){


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

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });





                    }
                });

            }
        });



        upload_rl = findViewById(R.id.upload_rl);
        upload_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();

            }
        });

        Spinner();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                storageRef.child("banners/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        // Got the download URL for 'users/me/profile.png'
                        final String spinner = my_spinner.getSelectedItem().toString();
                        System.out.println(spinner);

                        DatabaseReference userRef;
                        userRef = FirebaseDatabase.getInstance().getReference("users");

                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("banner").setValue(uri.toString());
                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("full name").setValue(full_name.getText().toString());
                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("shop name").setValue(shop_name.getText().toString());
                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("slogan").setValue(shop_slogan.getText().toString());
                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("email").setValue(email.getText().toString());
                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("password").setValue(password.getText().toString());
                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("phone number").setValue(phone);
                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("category").setValue(my_spinner.getSelectedItem().toString());

                        DatabaseReference shopRef;
                        shopRef = FirebaseDatabase.getInstance().getReference("shops");


                        shopRef.child("category").child(spinner).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("shop_name").setValue(shop_name.getText().toString());
                        shopRef.child("category").child(spinner).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("banner").setValue(uri.toString());


                        Intent intent = new Intent(CompleteForm.this, GpsSelector.class);
                        intent.putExtra("services",spinner);

                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Toast.makeText(CompleteForm.this, "Some error occurred. Try Again!", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });


    }




    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                upload.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        uploadFile();
    }
    //this method will upload the file
    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            String s = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child("banners/"+s+".jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            u.setText("File Upload Success");
                            u.setTextColor(getResources().getColor(R.color.green,null));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }


    public void Spinner(){


        mDatabase = FirebaseDatabase.getInstance().getReference("products");

        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, spinnerDataList);

        my_spinner.setAdapter(adapter);
        SpinnerData();

    }

    public void SpinnerData(){

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot item:dataSnapshot.getChildren()){

                    spinnerDataList.add(item.getKey());
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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