package com.cs.meetingbridge;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class CreateGroupActivity extends AppCompatActivity {
    private ListView selectedUsers;
    private EditText gName, userEmail;
    private ArrayList<String> selectedMembersNames;
    private ArrayList<userInfo> selectedMembers;
    private DatabaseReference databaseReference;
    private userInfo currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        selectedUsers = (ListView) findViewById(R.id.selectedUsers);
        gName = (EditText) findViewById(R.id.gName);
        userEmail = (EditText) findViewById(R.id.userEmail);
        Button searchButton = (Button) findViewById(R.id.searchButton);
        Button createGroupButton = (Button) findViewById(R.id.createGroupButton);
        selectedMembersNames = new ArrayList<>();
        selectedMembers = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(userInfo.class);
                selectedMembers.add(currentUser);
                selectedMembersNames.add(currentUser.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        addUsersInGroup(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupInfo groupInfo = new GroupInfo();

                groupInfo.setGroupName(gName.getText().toString());
                groupInfo.setMembersList(selectedMembers);
                groupInfo.setGroupId("1");

                databaseReference.child("Groups").push().setValue(groupInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateGroupActivity.this, "Group Created", Toast.LENGTH_SHORT).show();
                    }
                });

                databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        addID(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void addUsersInGroup(DataSnapshot dataSnapshot) {
        boolean userExist = false;
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            userInfo u = data.getValue(userInfo.class);
            String email = u.getEmail();
            if (email.equals(userEmail.getText().toString().trim()) && !searchArray(userEmail.getText().toString().trim())) {
                selectedMembersNames.add(u.getName());
                selectedMembers.add(u);
            }
        }
        if (searchArray(userEmail.getText().toString().trim())) {
            Toast.makeText(CreateGroupActivity.this, "User of this Email is added!", Toast.LENGTH_SHORT).show();
        } else if (!userExist) {
            Toast.makeText(CreateGroupActivity.this, "No User of this Email!", Toast.LENGTH_SHORT).show();
        }

        if (selectedMembersNames.size() > 0) {
            ArrayAdapter adapter = new ArrayAdapter(CreateGroupActivity.this, android.R.layout.simple_list_item_1, selectedMembersNames);
            selectedUsers.setAdapter(adapter);
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
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            GroupInfo g = data.getValue(GroupInfo.class);
            if (g.getGroupId().equals("1")) {
                String k = data.getKey();
                g.setGroupId(k);
                databaseReference.child("Groups").child(k).setValue(g);
                break;
            }
        }
    }


}

