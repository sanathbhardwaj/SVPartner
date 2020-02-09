package com.sanathbhardwaj.svpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skyfishjy.library.RippleBackground;

import java.util.List;
import java.util.Locale;

public class CurrentLocation extends AppCompatActivity implements GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {


    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private View mapView;
    private Button btnFind, select_location;
    private RippleBackground rippleBg;
    TextView current_Sub_Locality, current_address;
    ImageView back, back_dismiss;

    TextView locality, city, state, country;

    EditText address_line_1, address_line_2;
    private DatabaseReference mDatabase;
    private final float DEFAULT_ZOOM = 18;

    BottomSheetDialog bottomSheetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        bottomSheetDialog = new BottomSheetDialog(this);



        btnFind = findViewById(R.id.btn_find);
        rippleBg = findViewById(R.id.ripple_bg);

        rippleBg.startRippleAnimation();
        current_Sub_Locality = findViewById(R.id.current_Sub_Locality);
        current_address = findViewById(R.id.current_address);
        back = findViewById(R.id.back);

        ImageView imgMyLocation;
        imgMyLocation = (ImageView) findViewById(R.id.imgMyLocation);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        imgMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(CurrentLocation.this);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String services = getIntent().getStringExtra("services");

                final LatLng currentMarkerLocation = mMap.getCameraPosition().target;
                String Locality = getSubLocalityFromLatLng(currentMarkerLocation);
                final String City = getLocalityFromLatLng(currentMarkerLocation);
                String State = getStateFromLatLng(currentMarkerLocation);
                String Country = getCountryFromLatLng(currentMarkerLocation);
                final String Address = Locality + City + State + Country;

                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("Address").child("Locality").setValue(Locality);
                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("Address").child("City").setValue(City);
                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("Address").child("State").setValue(State);
                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("Address").child("country").setValue(Country);


                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("LatLog").setValue(currentMarkerLocation);
                mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("phone number").setValue(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());


                bottomSheetDialog.setContentView(R.layout.complete_address);
                bottomSheetDialog.show();
                select_location = bottomSheetDialog.findViewById(R.id.select_location);
                bottomSheetDialog.setCanceledOnTouchOutside(false);
                bottomSheetDialog.setCancelable(true);
                address_line_1 = bottomSheetDialog.findViewById(R.id.address_line_1);
                address_line_2 = bottomSheetDialog.findViewById(R.id.address_line_2);

                select_location.setEnabled(false);
                select_location.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                select_location.setBackgroundResource(R.drawable.button_drwable_light);

                locality = bottomSheetDialog.findViewById(R.id.Locality);
                city = bottomSheetDialog.findViewById(R.id.city);
                state = bottomSheetDialog.findViewById(R.id.state);
                country = bottomSheetDialog.findViewById(R.id.country);

                locality.setText(getSubLocalityFromLatLng(currentMarkerLocation));
                city.setText(getLocalityFromLatLng(currentMarkerLocation));
                state.setText(getStateFromLatLng(currentMarkerLocation));
                country.setText(getCountryFromLatLng(currentMarkerLocation));

                back_dismiss = bottomSheetDialog.findViewById(R.id.back_dismiss);
                back_dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });



                address_line_1.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {

                        if (s.toString().length()>1) {

                            address_line_2.addTextChangedListener(new TextWatcher() {

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before,
                                                          int count) {

                                    if (s.toString().length()>3) {
                                        select_location.setEnabled(true);
                                        select_location.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                                        select_location.setBackgroundResource(R.drawable.button_drwable);
                                    }
                                  else{
                                        select_location.setEnabled(false);
                                        select_location.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                                        select_location.setBackgroundResource(R.drawable.button_drwable_light);
                                    }
                                }

                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count,
                                                              int after) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });

                        } else {
                            select_location.setEnabled(false);
                            select_location.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null));
                            select_location.setBackgroundResource(R.drawable.button_drwable_light);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                select_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("Address").child("Address Line 1").setValue(address_line_1.getText().toString());
                        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("partner").child("Address").child("Address Line 2").setValue(address_line_2.getText().toString());

                        bottomSheetDialog.dismiss();
                        Intent intent = new Intent(CurrentLocation.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("LatLog", currentMarkerLocation);
                        intent.putExtra("Address", Address);
                        intent.putExtra("City", City);
                        intent.putExtra("services", services);
                        startActivity(intent);
                        finish();

                    }
                });



//                LatLng currentMarkerLocation = mMap.getCameraPosition().target;
//                String you = currentMarkerLocation.toString();
//                current_location_tv.setText(you);

            }
        });


    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
//            Toast.makeText(this, "The user gestured on the map.",
//                    Toast.LENGTH_SHORT).show();
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
//            Toast.makeText(this, "The user tapped something on the map.",
//                    Toast.LENGTH_SHORT).show();
        } else if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
//            Toast.makeText(this, "The app moved the camera.",
//                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCameraMove() {
        current_Sub_Locality.setText("locating...");
    }

    @Override
    public void onCameraMoveCanceled() {
//        Toast.makeText(this, "Camera movement canceled.",
//                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraIdle() {

        LatLng currentMarkerLocation = mMap.getCameraPosition().target;
        String SubLocality = getSubLocalityFromLatLng(currentMarkerLocation);
        String city = getLocalityFromLatLng(currentMarkerLocation);
        String state = getStateFromLatLng(currentMarkerLocation);
        String country = getCountryFromLatLng(currentMarkerLocation);
        String Address = SubLocality+" "+city+", "+state+", "+country;
        current_Sub_Locality.setText(SubLocality);
        current_address.setText(Address);
//        String you = currentMarkerLocation.toString();
//        current_location_tv.setText(you);

    }
    private String getSubLocalityFromLatLng(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//            String address = addresses.get(0).getAddressLine(0);
//            current_location_tv.setText(address);
            return addresses.get(0).getSubLocality();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getLocalityFromLatLng(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//            String address = addresses.get(0).getAddressLine(0);
//            current_location_tv.setText(address);
            return addresses.get(0).getLocality();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getStateFromLatLng(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//            String address = addresses.get(0).getAddressLine(0);
//            current_location_tv.setText(address);
            return addresses.get(0).getAdminArea();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getCountryFromLatLng(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//            String address = addresses.get(0).getAddressLine(0);
//            current_location_tv.setText(address);
            return addresses.get(0).getCountryName();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//            String address = addresses.get(0).getAddressLine(0);
//            current_location_tv.setText(address);
            return addresses.get(0).getAddressLine(0);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);

        // Show Sydney on the map.
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(23.8051, 86.427793), 10));

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 50, 50);
        }

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(CurrentLocation.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(CurrentLocation.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(CurrentLocation.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(CurrentLocation.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(CurrentLocation.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
