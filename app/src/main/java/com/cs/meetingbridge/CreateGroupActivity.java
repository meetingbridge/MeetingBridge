package com.cs.meetingbridge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    private ArrayList<String> selectedUsersList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        selectedUsers = (ListView) findViewById(R.id.selectedUsers);
        gName = (EditText) findViewById(R.id.gName);
        userEmail = (EditText) findViewById(R.id.userEmail);
        searchButton = (Button) findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addValueEventListener(new ValueEventListener() {
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
    }

    private void addDatainListView(DataSnapshot dataSnapshot) {
        selectedUsersList.clear();
//String str= "haseeb@gmail.com";

        for (DataSnapshot data : dataSnapshot.getChildren()) {
            userInfo u = new userInfo();
            //u = data.getValue(userInfo.class);
            u.setEmail(data.getValue(userInfo.class).getEmail());

            selectedUsersList.add(u.getEmail());

        }
        if (selectedUsersList.size() > 0) {
            for (int i = 0; i < selectedUsersList.size(); i++) {

                if (selectedUsersList.get(i).equals(userEmail.getText())) {
                    Toast.makeText(CreateGroupActivity.this, "User Exists", Toast.LENGTH_LONG).show();
                    return;
                } else if (i == (selectedUsersList.size() - 1)) {
                    Toast.makeText(CreateGroupActivity.this, "Does Not", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(CreateGroupActivity.this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }
}
