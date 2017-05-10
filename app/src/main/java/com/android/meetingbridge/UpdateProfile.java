package com.android.meetingbridge;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UpdateProfile extends AppCompatActivity {

    DatabaseReference databaseReference;
    private StorageReference storageReference;
    private EditText fullName, contactNo, gender, email;
    private Uri uri;
    private ImageView hostIcon;
    private int GALLERY_INTENT = 2;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fullName = (EditText) findViewById(R.id.fullname);
        contactNo = (EditText) findViewById(R.id.contactno);
        gender = (EditText) findViewById(R.id.gender);
        hostIcon = (ImageView) findViewById(R.id.icon);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        FloatingActionButton pickImage = (FloatingActionButton) findViewById(R.id.fabInfo);
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
        final Button submit = (Button) findViewById(R.id.submit);

        databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final userInfo u = dataSnapshot.getValue(userInfo.class);
                fullName.setText(u.getName());
                contactNo.setText(u.getContactNum());
                gender.setText(u.getGender());

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(fullName.getText())) {
                            fullName.setError("Enter Full Name");
                            return;
                        }

                        if (TextUtils.isEmpty(contactNo.getText())) {
                            contactNo.setError("Enter Your Active Contact No");
                            return;
                        }

                        if (TextUtils.isEmpty(gender.getText())) {
                            Toast.makeText(UpdateProfile.this, "Select Gender", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (uri == null) {
                            Toast.makeText(UpdateProfile.this, "Select Profile Picture", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        final userInfo user_Info = new userInfo(u.getId(), fullName.getText().toString(), contactNo.getText().toString(), gender.getText().toString(), u.getEmail(), u.getLat(), u.getLng());
                        databaseReference.child("Users").child(u.getId()).setValue(user_Info).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Great " + user_Info.getName() + "! Your Profile has been Updated", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    finish();
                                }
                            }
                        });
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        storageReference.child("Photos").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri)
                        .resize(200, 200).centerCrop().transform(new CircleTransform()).into(hostIcon);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            uri = data.getData();
            System.out.println(uri);
            ImageView profilePic = (ImageView) findViewById(R.id.icon);
            Picasso.with(UpdateProfile.this)
                    .load(uri)
                    .resize(200, 200).centerCrop()
                    .transform(new CircleTransform()).into(profilePic);
            storageReference.child("Photos").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).putFile(uri);
            //Ahh
        }
    }
}
