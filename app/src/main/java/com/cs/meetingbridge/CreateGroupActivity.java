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
    private ArrayList<userInfo> selectedUsersList = null;
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
                        if (addDatainListView(dataSnapshot)) {
                            Toast.makeText(CreateGroupActivity.this, "Hurrah!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateGroupActivity.this, "Fuck", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private boolean addDatainListView(DataSnapshot dataSnapshot) {
        //Toast.makeText(CreateGroupActivity.this, userEmail.getText(), Toast.LENGTH_LONG).show();

        for (DataSnapshot data : dataSnapshot.getChildren()) {
            userInfo u = data.getValue(userInfo.class);
            String email = u.getEmail();
            if (email.equals(userEmail.getText().toString())) {
                return true;
            }
        }
        return false;
    }
}
