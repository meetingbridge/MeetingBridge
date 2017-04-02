package com.android.meetingbridge;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AdditionalInfoActivity extends PermissionClass implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int GALLERY_INTENT = 2;
    private static final int REQUEST_PERMISSION = 10;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    private EditText fullName, contactNo;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private String gender;
    private StorageReference storageReference;
    private LocationRequest mLocationRequest;
    //URI
    private Uri uri;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        requestAppPermission(new String[]
                        {android.Manifest.permission.READ_CONTACTS,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_CONTACTS},
                R.string.permission_msg, REQUEST_PERMISSION);
        setContentView(R.layout.activity_additional_info);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fullName = (EditText) findViewById(R.id.fullname);
        contactNo = (EditText) findViewById(R.id.contactno);
        final RadioGroup genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
        Button submit = (Button) findViewById(R.id.submit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        TextView welcome = (TextView) findViewById(R.id.welcome);
        welcome.setText("Welcome " + user.getEmail() + "!");
        if (user == null) {
            startActivity(new Intent(AdditionalInfoActivity.this, LoginActivity.class));
        }

        assert genderGroup != null;
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.male) {
                    gender = "Male";
                } else if (checkedId == R.id.female) {
                    gender = "Female";
                }
            }
        });

        FloatingActionButton pickImage = (FloatingActionButton) findViewById(R.id.fabInfo);
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        assert submit != null;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = fullName.getText().toString().trim();
                final String contact = contactNo.getText().toString().trim();
                final String email = user.getEmail();
                if (TextUtils.isEmpty(name)) {
                    fullName.setError("Enter Full Name");
                    return;
                }

                if (TextUtils.isEmpty(contact)) {
                    contactNo.setError("Enter Your Active Contact No");
                    return;
                }

                if (TextUtils.isEmpty(gender)) {
                    Toast.makeText(AdditionalInfoActivity.this, "Select Gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (uri == null) {
                    Toast.makeText(AdditionalInfoActivity.this, "Select Profile Picture", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                mLocationRequest = new LocationRequest();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            userInfo user_Info = new userInfo(user.getUid(), name, contact, gender, email, location.getLatitude(), location.getLongitude());
                            databaseReference.child("Users").child(user.getUid()).setValue(user_Info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(AdditionalInfoActivity.this, "oops", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(AdditionalInfoActivity.this, "Great " + name + "! Your Profile has been Updated", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AdditionalInfoActivity.this, HomeActivity.class));
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Allow Application to Check your Location!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            uri = data.getData();
            System.out.println(uri);
            ImageView profilePic = (ImageView) findViewById(R.id.icon);
            Picasso.with(AdditionalInfoActivity.this)
                    .load(uri)
                    .resize(200, 200).centerCrop()
                    .transform(new CircleTransform()).into(profilePic);
            storageReference.child("Photos").child(user.getUid()).putFile(uri);
        }
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}