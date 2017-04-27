package com.android.meetingbridge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.text.TextUtils.isEmpty;

public class CreatePublicPostActivity extends AppCompatActivity {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private DatabaseReference databaseReference;
    private Button getPostLocation;
    private Place postPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_public_post);
        checkNetwork();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final EditText postTitle = (EditText) findViewById(R.id.postTitleET);
        final EditText postDescription = (EditText) findViewById(R.id.postDescriptionET);
        final TextView timeView = (TextView) findViewById(R.id.postTimeTV);
        final TextView dateView = (TextView) findViewById(R.id.postDateTV);
        final Button postButton = (Button) findViewById(R.id.postButton);
        final PostTime postTime = new PostTime();
        final PostDate postDate = new PostDate();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Button backbutton = (Button) findViewById(R.id.btnback);
        getPostLocation = (Button) findViewById(R.id.getPostLocation);

        getPostLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPlaceAutocompleteActivityIntent();
            }
        });


        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "content_create_post");
                //(getFragmentManager(),"post_layout");
            }
        });
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "content_post_create");
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        databaseReference.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final userInfo user = dataSnapshot.getValue(userInfo.class);

                postButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkNetwork();
                        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.VISIBLE);
                        if (isEmpty(postTitle.getText())) {
                            postTitle.setError("Enter Event Title");
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        if (isEmpty(postDescription.getText())) {
                            postDescription.setError("Enter Event Details");
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        if (isEmpty(timeView.getText()) || timeView.getText().equals("Select Event Time")) {
                            timeView.setError("Select Event Time");
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Select Event Time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (isEmpty(dateView.getText()) || dateView.getText().equals("Select Event Date")) {
                            dateView.setError("Select Event Date");
                            Toast.makeText(getApplicationContext(), "Select Event Date", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        final String currentTime = new SimpleDateFormat("dd-M-yy hh:mm a").format(new Date());
                        final String title = postTitle.getText().toString();
                        final String description = postDescription.getText().toString();
                        String timeTemp = timeView.getText().toString();
                        String dateTemp = dateView.getText().toString();
                        String[] timeArray = timeTemp.split(" ");
                        String[] dateArray = dateTemp.split(" ");
                        postTime.setHours(timeArray[0]);
                        postTime.setMinutes(timeArray[1]);
                        postTime.setAmpm(timeArray[2]);
                        postDate.setDay(dateArray[0]);
                        postDate.setMonth(dateArray[1]);
                        postDate.setYear(dateArray[2]);
                        try {

                            PostPlaceInfo postPlaceInfo = new PostPlaceInfo(postPlace.getId(),
                                    postPlace.getName().toString(),
                                    String.valueOf(postPlace.getLatLng().latitude),
                                    String.valueOf(postPlace.getLatLng().longitude),
                                    postPlace.getAddress().toString(),
                                    postPlace.getPhoneNumber().toString());
                            PostInfo postInfo = new PostInfo("1", "1", title, description, postPlaceInfo, postTime, postDate, user, currentTime);
                            databaseReference.child("publicmeetup").push().setValue(postInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    databaseReference.child("publicmeetup").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            addID(dataSnapshot);
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(new Intent(CreatePublicPostActivity.this, HomeActivity.class));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                        } catch (Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Select Event Venue", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addID(DataSnapshot dataSnapshot) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            PostInfo p = data.getValue(PostInfo.class);
            if (String.valueOf(p.getPostId()).equals("1")) {
                String k = data.getKey();
                p.setPostId(k);
                databaseReference.child("publicmeetup").child(k).setValue(p);
                break;
            }

        }
    }

    private void checkNetwork() {
        if (!isNetworkAvailable()) {
            AlertDialog.Builder CheckBuilder = new AlertDialog.Builder(this);
            CheckBuilder.setCancelable(false);
            CheckBuilder.setTitle("Error!");
            CheckBuilder.setMessage("Check Your Internet Connection!");

            CheckBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
            CheckBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            AlertDialog alert = CheckBuilder.create();
            alert.show();
        } else {
            if (isNetworkAvailable()) {
                //Toast.makeText(this, "Internet Available", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //autocompleteFragment.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                postPlace = PlaceAutocomplete.getPlace(this, data);
                String str = postPlace.getName().toString() + " " + postPlace.getAddress().toString();
                getPostLocation.setText(str);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if (requestCode == RESULT_CANCELED) {

            }
        }
    }
}