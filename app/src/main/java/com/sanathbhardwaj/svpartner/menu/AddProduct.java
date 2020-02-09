package com.sanathbhardwaj.svpartner.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.sanathbhardwaj.svpartner.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddProduct extends AppCompatActivity {

    EditText product_name, product_price, product_stock, product_description;
    Button submit, add_another_product;
    private DatabaseReference mDatabase;
    private DatabaseReference getmDatabase;
    private DatabaseReference productDatabase;
    ImageView product_image;
    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri filePath;

    Spinner my_spinner;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);



        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        product_stock = findViewById(R.id.product_stock);
        product_description = findViewById(R.id.product_description);
        submit = findViewById(R.id.submit);
        add_another_product = findViewById(R.id.add_another_product);
        product_image = findViewById(R.id.product_img);
        getmDatabase = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("category");


        getmDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s = dataSnapshot.getValue().toString();
                productDatabase = FirebaseDatabase.getInstance().getReference("shops").child("category").child(s).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Spinner(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



       product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });



//        final AwesomeSpinner my_spinner = findViewById(R.id.my_spinner);
//        List<String> categories = new ArrayList<>();
//        categories.add("Atta | Flours | Sooji");
//        categories.add("Rice | Rice Products");
//        categories.add("Dals | Pulses");
//        categories.add("Salt | Sugar | Jaggery");
//        categories.add("Masalas | Spices");
//        categories.add("Dry Fruits");
//        categories.add("Oils | Ghee");
//        categories.add("Beverages");
//        categories.add("Bakery | Cake | Dairy Products");
//        categories.add("Beauty | Hygiene");
//        categories.add("Snacks | Branded Products");
//        categories.add("Beauty | Hygiene");
//        categories.add("Snacks | Branded Products");
//        categories.add("Baby Products");
//        categories.add("Others");
//
//        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
//        my_spinner.setAdapter(categoriesAdapter);
//        my_spinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
//            @Override
//            public void onItemSelected(int position, String itemAtPosition) {
//
//                System.out.println(position);
//                System.out.println(itemAtPosition);
//            }
//        });

       add_another_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String currentDate = new SimpleDateFormat("MMddyyyy", Locale.getDefault()).format(new Date());
                final String currentTime = new SimpleDateFormat("HHMMSS", Locale.getDefault()).format(new Date());
                final String productname = currentDate+currentTime+FirebaseAuth.getInstance().getCurrentUser().getUid();
                final String spinner = my_spinner.getSelectedItem().toString();


                mDatabase.child(spinner).child("image").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        productDatabase.child("category").child(spinner).child("image").setValue(dataSnapshot.getValue());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                uploadFile(productname, spinner);

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
                product_image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //this method will upload the file
    private void uploadFile(final String productname, final String my_spinner) {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child("productimage/"+productname+".jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            getDownloadUrl();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "Product added successfully ! ", Toast.LENGTH_LONG).show();
                            product_name.setText("");
                            product_price.setText("");
                            product_stock.setText("");
                            product_description.setText("");
                            product_image.setImageResource(R.drawable.rename);

                        }

                        private void getDownloadUrl() {



                            productDatabase.child("category").child(my_spinner).child("name").setValue(my_spinner);
                            productDatabase.child("category").child(my_spinner).child("products").child(productname).child("product_name").setValue(product_name.getText().toString());
                            productDatabase.child("category").child(my_spinner).child("products").child(productname).child("product_price").setValue(product_price.getText().toString());
                            productDatabase.child("category").child(my_spinner).child("products").child(productname).child("product_stock").setValue(product_stock.getText().toString());
                            productDatabase.child("category").child(my_spinner).child("products").child(productname).child("product_description").setValue(product_description.getText().toString());


                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();


                            storageRef.child("productimage/"+productname+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {


                                    productDatabase.child("category").child(my_spinner).child("products").child(productname).child("product_image").setValue(uri.toString());
                                    // Got the download URL for 'users/me/profile.png'

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    Toast.makeText(AddProduct.this, "Some error occurred. Try Again!", Toast.LENGTH_SHORT).show();
                                    System.out.println(productname);

                                }
                            });



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

    public void Spinner(String s){


        mDatabase = FirebaseDatabase.getInstance().getReference("products").child(s);

        my_spinner = findViewById(R.id.my_spinner);

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
}
