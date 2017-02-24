package com.android.meetingbridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
        setContentView(R.layout.content_create_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        final EditText postTitle = (EditText) findViewById(R.id.postTitleET);
        final EditText postDescription = (EditText) findViewById(R.id.postDescriptionET);
        final TextView timeView = (TextView) findViewById(R.id.postTimeTV);
        final TextView groupNameTV = (TextView) findViewById(R.id.groupNameTV);
        final TextView dateView = (TextView) findViewById(R.id.postDateTV);
        final Button postButton = (Button) findViewById(R.id.postButton);
        final PostTime postTime = new PostTime();
        final PostDate postDate = new PostDate();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Button backbutton = (Button) findViewById(R.id.btnback);
        final EditText postLocation = (EditText) findViewById(R.id.postLocationET);

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

        final String id = getIntent().getStringExtra("id");
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatePostActivity.this, GroupActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<GroupInfo> groupInfos = getCurrentGroup(dataSnapshot);
                int temp = Integer.parseInt(id);
                groupNameTV.setText(groupInfos.get(temp).getGroupName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseReference.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final userInfo user = dataSnapshot.getValue(userInfo.class);

                databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<GroupInfo> groupInfos = getCurrentGroup(dataSnapshot);
                        final int temp = Integer.parseInt(id);
                        postButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                                progressBar.setVisibility(View.VISIBLE);
                                final String currentTime = new SimpleDateFormat("dd-M-yy hh:mm a").format(new Date());
                                final String title = postTitle.getText().toString();
                                final String description = postDescription.getText().toString();
                                final String location = postLocation.getText().toString();
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
                                final PostInfo postInfo = new PostInfo("1", title, description, location, postTime, postDate, user, currentTime, groupInfos.get(temp));

                                databaseReference.child("Posts").push().setValue(postInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(CreatePostActivity.this, "Posted!", Toast.LENGTH_SHORT).show();

                                        databaseReference.child("Posts").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                addID(dataSnapshot);
                                                progressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(CreatePostActivity.this, GroupActivity.class);
                                                intent.putExtra("id", id);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
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

    private void addID(DataSnapshot dataSnapshot) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            PostInfo p = data.getValue(PostInfo.class);
            if (String.valueOf(p.getPostId()).equals("1")) {
                String k = data.getKey();
                p.setPostId(k);
                databaseReference.child("Posts").child(k).setValue(p);
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