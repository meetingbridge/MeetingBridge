package com.android.meetingbridge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//
public class CreateGroupActivity extends AppCompatActivity {
    private ListView selectedUsersLV;
    private EditText gName, userEmail;
    private ArrayList<userInfo> selectedMembers;
    private DatabaseReference databaseReference;
    private userInfo currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_create_group);
        checkNetwork();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        selectedUsersLV = (ListView) findViewById(R.id.selectedUsers);
        gName = (EditText) findViewById(R.id.gName);
        userEmail = (EditText) findViewById(R.id.userEmail);
        final Button searchButton = (Button) findViewById(R.id.searchButton);
        final Button createGroupButton = (Button) findViewById(R.id.createGroupButton);
        selectedMembers = new ArrayList<>();
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentUser = dataSnapshot.getValue(userInfo.class);
                if (!searchArray(currentUser.getEmail())) {
                    selectedMembers.add(0, currentUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkNetwork();
                        progressBar.setVisibility(View.VISIBLE);
                        addUsersInGroup(dataSnapshot, userEmail.getText().toString());
                        progressBar.setVisibility(View.GONE);

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()) {
                    GroupInfo groupInfo = new GroupInfo(selectedMembers, gName.getText().toString(), "id");
                    progressBar.setVisibility(View.VISIBLE);

                    databaseReference.child("Groups").push().setValue(groupInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    addID(dataSnapshot);
                                    Toast.makeText(CreateGroupActivity.this, "Group Created! Open it from Drawer!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(new Intent(CreateGroupActivity.this, HomeActivity.class));
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });


                } else {
                    checkNetwork();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
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

    private void addUsersInGroup(DataSnapshot dataSnapshot, String emailText) {
        boolean userExist = false;
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            userInfo u = data.getValue(userInfo.class);
            String email = u.getEmail().toLowerCase();

            if (email.equals(emailText.toLowerCase()) && !searchArray(emailText.toLowerCase())) {
                selectedMembers.add(0, u);
                userExist = true;
            }
        }
        if (searchArray(emailText.toLowerCase())) {
            Toast.makeText(CreateGroupActivity.this, "User " + emailText + " is added!", Toast.LENGTH_SHORT).show();
        } else if (!userExist) {

            Toast.makeText(CreateGroupActivity.this, "No User of this Email!", Toast.LENGTH_SHORT).show();
        }

        if (selectedMembers.size() > 0) {
            UserListAdapter adapter = new UserListAdapter(CreateGroupActivity.this, selectedMembers);
            selectedUsersLV.setAdapter(adapter);
        }
        userEmail.setText(null);
    }

    private boolean searchArray(String email) {
        for (int i = 0; i < selectedMembers.size(); i++) {
            if (email.equals(selectedMembers.get(i).getEmail())) {
                return true;
            }
        }
        return false;
    }

    private void addID(DataSnapshot dataSnapshot) {
        for (DataSnapshot d : dataSnapshot.getChildren()) {
            GroupInfo g = d.getValue(GroupInfo.class);
            if (g.getGroupId().equals("id")) {
                String k = d.getKey();
                g.setGroupId(k);
                databaseReference.child("Groups").child(k).setValue(g);
                break;
            }

        }
    }

}

