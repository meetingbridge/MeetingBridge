package com.cs.meetingbridge;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AdditionalInfoActivity extends AppCompatActivity {
    private static final int GALLERY_INTENT = 2;
    private EditText fullName, contactNo;
    private FirebaseUser user;
    private TextView welcome;
    private Button pickImage;
    private DatabaseReference databaseReference;
    private String gender;
    private String gener;
    private StorageReference imgStorage;
    private StorageReference filePath;

    //URI
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        fullName = (EditText) findViewById(R.id.fullname);
        contactNo = (EditText) findViewById(R.id.contactno);
        final RadioGroup genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
        Button submit = (Button) findViewById(R.id.submit);
        pickImage = (Button) findViewById(R.id.pickImage);
        welcome = (TextView) findViewById(R.id.welcome);
        welcome.setText(user.getEmail());
        if (user == null) {
            startActivity(new Intent(AdditionalInfoActivity.this, LoginActivity.class));
        }
        assert genderGroup != null;
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.male) {
                    Toast.makeText(AdditionalInfoActivity.this, "Male", Toast.LENGTH_SHORT).show();
                    gender = "Male";
                } else if (checkedId == R.id.female) {
                    gender = "Female";
                }
            }
        });

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

                filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(AdditionalInfoActivity.this, "Ooops", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdditionalInfoActivity.this, "Profile Picture Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                userInfo user_Info = new userInfo(name, contact, gender);
                databaseReference.child(user.getUid()).setValue(user_Info).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(AdditionalInfoActivity.this, "oops", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdditionalInfoActivity.this, "Great " + name + "! Your Profile has been Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            uri = data.getData();
            filePath = imgStorage.child("photos").child(user.getUid()).child(uri.getLastPathSegment());
        }
    }
}