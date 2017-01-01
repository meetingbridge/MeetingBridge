package com.cs.meetingbridge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    private Button searchButton;
    private ArrayList<String> selectedMembersNames;
    private ArrayList<userInfo> selectedMembers;
    private DatabaseReference databaseReference;
    private Button createGroupButton;
    private FirebaseUser user;
    private userInfo currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(userInfo.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        selectedUsers = (ListView) findViewById(R.id.selectedUsers);
        gName = (EditText) findViewById(R.id.gName);
        userEmail = (EditText) findViewById(R.id.userEmail);
        searchButton = (Button) findViewById(R.id.searchButton);
        createGroupButton = (Button) findViewById(R.id.createGroupButton);
        selectedMembersNames = new ArrayList<>();
        selectedMembers = new ArrayList<>();
        selectedMembers.add(currentUser);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        addDatainListView(dataSnapshot);
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
                databaseReference.child("Groups").push().setValue(groupInfo);
            }
        });
    }

    private void addDatainListView(DataSnapshot dataSnapshot) {

        for (DataSnapshot data : dataSnapshot.getChildren()) {
            userInfo u = data.getValue(userInfo.class);
            String email = u.getEmail();
            if (email.equals(userEmail.getText().toString().trim()) && !searchArray(userEmail.getText().toString().trim())) {
                selectedMembersNames.add(u.getName());
                selectedMembers.add(u);
            }
        }
        if (selectedMembersNames.size() > 0) {
            ArrayAdapter adapter = new ArrayAdapter(CreateGroupActivity.this, android.R.layout.simple_list_item_1, selectedMembersNames);
            selectedUsers.setAdapter(adapter);
        } else {
            Toast.makeText(CreateGroupActivity.this, "No User of this Email!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean searchArray(String email) {
        for (int i = 0; i <= selectedMembers.size(); i++) {
            if (email.equals(selectedMembers.get(i).getEmail())) {
                return true;
            }
        }
        return false;
    }
}

