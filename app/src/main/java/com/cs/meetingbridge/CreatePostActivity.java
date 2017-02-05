
package com.cs.meetingbridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreatePostActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        final EditText postTitle = (EditText) findViewById(R.id.postTitleET);
        final EditText postDescription = (EditText) findViewById(R.id.postDescriptionET);
        Button setTimeButton = (Button) findViewById(R.id.setTime);
        Button setDateButton = (Button) findViewById(R.id.setDate);
        final TextView timeView = (TextView) findViewById(R.id.postTimeTV);
        final TextView dateView = (TextView) findViewById(R.id.postDateTV);
        Button postButton = (Button) findViewById(R.id.postButton);
        final PostTime postTime = new PostTime();
        final PostDate postDate = new PostDate();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "content_create_post");
                //(getFragmentManager(),"post_layout");
            }
        });
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "content_post_create");
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        final String id = getIntent().getStringExtra("id");
//

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                databaseReference.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final userInfo user = dataSnapshot.getValue(userInfo.class);

                        databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<GroupInfo> groupInfos = getCurrentGroup(dataSnapshot);
                                int temp = Integer.parseInt(id);
                                final String groupId = groupInfos.get(temp).getGroupId();
                                final PostInfo postInfo = new PostInfo("1", title, description, postTime, postDate, user, currentTime, groupInfos.get(temp));
                                databaseReference.child("Posts").child(groupId).push().setValue(postInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(CreatePostActivity.this, "Posted!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                databaseReference.child("Posts").child(groupId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        addID(dataSnapshot, groupId);
                                        Intent intent = new Intent(CreatePostActivity.this, GroupActivity.class);
                                        intent.putExtra("id", id);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


    }

    private void addID(DataSnapshot dataSnapshot, String groupId) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            PostInfo p = data.getValue(PostInfo.class);
            if (String.valueOf(p.getPostId()).equals("1")) {
                String k = data.getKey();
                p.setPostId(k);
                databaseReference.child("Posts").child(groupId).child(k).setValue(p);
                break;
            }

        }
    }

    private ArrayList<GroupInfo> getCurrentGroup(DataSnapshot dataSnapshot) {
        ArrayList<GroupInfo> groupInfos = new ArrayList<>();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            GroupInfo g = data.getValue(GroupInfo.class);
            ArrayList<userInfo> userInfos = g.getMembersList();
            for (int i = 0; i < userInfos.size(); i++) {
                if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(userInfos.get(i).getEmail())) {
                    groupInfos.add(g);
                }
            }
        }
        return groupInfos;
    }


}