package com.android.meetingbridge;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PrivatePostListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PostInfo> mPostList;

    public PrivatePostListAdapter(Context mContext, ArrayList<PostInfo> mPostList) {
        this.mContext = mContext;
        this.mPostList = mPostList;
    }

    @Override
    public int getCount() {
        return mPostList.size();
    }

    @Override
    public Object getItem(int i) {
        return mPostList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        final View v = View.inflate(mContext, R.layout.post_layout, null);
        PostTime time = mPostList.get(i).getPostTime();
        final PostDate date = mPostList.get(i).getPostDate();
        TextView postingTime = (TextView) v.findViewById(R.id.postingTime);

        String timeString = time.getHours() + ":" + time.getMinutes() + " " + time.getAmpm();
        String dateString = date.getDay() + "/" + date.getMonth() + "/" + date.getYear();
        TextView Name = (TextView) v.findViewById(R.id.hostName);
        final ImageView postIcon = (ImageView) v.findViewById(R.id.postIcon);
        TextView Title = (TextView) v.findViewById(R.id.postTitle);
        TextView groupName = (TextView) v.findViewById(R.id.groupName);
        TextView Discription = (TextView) v.findViewById(R.id.postDiscription);
        ImageView postedIn = (ImageView) v.findViewById(R.id.postedIn);
        TextView TimeView = (TextView) v.findViewById(R.id.Time);
        TextView DateView = (TextView) v.findViewById(R.id.Date);
        TextView locationTV = (TextView) v.findViewById(R.id.postLocationTV);
        try {
            Thread myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    storageReference.child("Photos").child(mPostList.get(i).getHost().getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(v.getContext()).load(uri)
                                    .resize(200, 200).centerCrop().into(postIcon);
                        }
                    });
                }
            });
            myThread.setPriority(Thread.MAX_PRIORITY);
            myThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        postIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.user_profile_layout);
                //dialog.setTitle(users.get(position).getName());

                final ImageView userProfile = (ImageView) dialog.findViewById(R.id.profileImage);
                TextView userName = (TextView) dialog.findViewById(R.id.profileName);
                TextView userEmail = (TextView) dialog.findViewById(R.id.profileEmail);
                TextView userContact = (TextView) dialog.findViewById(R.id.profileContact);
                TextView userGender = (TextView) dialog.findViewById(R.id.profileGender);
                userName.setText(mPostList.get(i).getHost().getName());
                userContact.setText(mPostList.get(i).getHost().getContactNum());
                userEmail.setText(mPostList.get(i).getHost().getEmail());
                userGender.setText(mPostList.get(i).getHost().getGender());
                Button update = (Button) dialog.findViewById(R.id.updateprofile);
                if (mPostList.get(i).getHost().getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    update.setVisibility(View.VISIBLE);
                }
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, UpdateProfile.class));
                    }
                });
                Thread myThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        storageReference.child("Photos").child(mPostList.get(i).getHost().getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(dialog.getContext()).load(uri)
                                        .resize(200, 200).centerCrop().transform(new CircleTransform()).into(userProfile);
                            }
                        });
                    }
                });
                myThread.setPriority(Thread.MAX_PRIORITY);
                myThread.start();

                dialog.show();
            }
        });
        Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.user_profile_layout);
                //dialog.setTitle(users.get(position).getName());

                final ImageView userProfile = (ImageView) dialog.findViewById(R.id.profileImage);
                TextView userName = (TextView) dialog.findViewById(R.id.profileName);
                TextView userEmail = (TextView) dialog.findViewById(R.id.profileEmail);
                TextView userContact = (TextView) dialog.findViewById(R.id.profileContact);
                TextView userGender = (TextView) dialog.findViewById(R.id.profileGender);
                userName.setText(mPostList.get(i).getHost().getName());
                userContact.setText(mPostList.get(i).getHost().getContactNum());
                userEmail.setText(mPostList.get(i).getHost().getEmail());
                userGender.setText(mPostList.get(i).getHost().getGender());
                Button update = (Button) dialog.findViewById(R.id.updateprofile);
                if (mPostList.get(i).getHost().getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    update.setVisibility(View.VISIBLE);
                }
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, UpdateProfile.class));
                    }
                });
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                storageReference.child("Photos").child(mPostList.get(i).getHost().getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(dialog.getContext()).load(uri)
                                .resize(200, 200).centerCrop().transform(new CircleTransform()).into(userProfile);
                    }
                });
                dialog.show();
            }
        });
        String ab = mPostList.get(i).getHost().getName() + " ";
        Name.setText(ab);

        Title.setText(mPostList.get(i).getPostTitle());
        String location = mPostList.get(i).getPostLocation().getName() + " " + mPostList.get(i).getPostLocation().getAddress();
        locationTV.setText(location);
        Discription.setText(mPostList.get(i).getPostDescription());
        TimeView.setText(timeString);
        DateView.setText(dateString);
        postingTime.setText(mPostList.get(i).getPostingTime());
        v.setTag(mPostList.get(i).getPostId());
        String str = " " + mPostList.get(i).getGroupInfo().getGroupName();
        groupName.setText(str);
        if (mContext.getClass().toString().equals("class com.android.meetingbridge.HomeActivity")) {
            groupName.setVisibility(View.VISIBLE);
            postedIn.setVisibility(View.VISIBLE);
        }
        if (mPostList.get(i).getPostType().equals("2")) {
            v.setBackgroundColor(Color.parseColor("#B3E5FC"));
        }

        final Button commentButton = (Button) v.findViewById(R.id.commentsButton);


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.comment_dialog_layout);
                dialog.setTitle(mPostList.get(i).getPostTitle());
                final EditText commentBox = (EditText) dialog.findViewById(R.id.postCommentET);
                final ListView commentLV = (ListView) dialog.findViewById(R.id.commentsLV);
                Button dismissButton = (Button) dialog.findViewById(R.id.dismiss);
                final Button postCommentButton = (Button) dialog.findViewById(R.id.postCommentButton);

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                final String currentTime = new SimpleDateFormat("dd-M-yy hh:mm a").format(new Date());
                databaseReference.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final userInfo user = dataSnapshot.getValue(userInfo.class);
                        databaseReference.child("Comments")
                                .child(mPostList.get(i).getGroupInfo().getGroupId())
                                .child(mPostList.get(i).getPostId())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final ArrayList<CommentInfo> commentInfos;
                                        if (mPostList.get(i).getPostType().equals("2")) {
                                            if (mPostList.get(i).getHost().getEmail().equals(user.getEmail())) {
                                                commentInfos = fetchComments(dataSnapshot);
                                            } else {
                                                commentInfos = fetchSecretComments(dataSnapshot, user, mPostList.get(i).getHost());
                                            }
                                        } else {
                                            commentInfos = fetchComments(dataSnapshot);
                                        }
                                        commentLV.setAdapter(makeAdapter(v.getContext(), commentInfos));
                                        postCommentButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(final View v) {
                                                checkNetwork();
                                                CommentInfo commentInfo = new CommentInfo(currentTime, user, commentBox.getText().toString(), mPostList.get(i).getGroupInfo());

                                                databaseReference.child("Comments")
                                                        .child(mPostList.get(i).getGroupInfo().getGroupId())
                                                        .child(mPostList.get(i).getPostId()).push().setValue(commentInfo);
                                                commentBox.setText("");
                                                commentLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        final Dialog dialog = new Dialog(v.getContext());
                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialog.setContentView(R.layout.user_profile_layout);
                                                        //dialog.setTitle(users.get(position).getName());

                                                        final ImageView userProfile = (ImageView) dialog.findViewById(R.id.profileImage);
                                                        TextView userName = (TextView) dialog.findViewById(R.id.profileName);
                                                        TextView userEmail = (TextView) dialog.findViewById(R.id.profileEmail);
                                                        TextView userContact = (TextView) dialog.findViewById(R.id.profileContact);
                                                        TextView userGender = (TextView) dialog.findViewById(R.id.profileGender);
                                                        userName.setText(commentInfos.get(position).getHost().getName());
                                                        userContact.setText(commentInfos.get(position).getHost().getContactNum());
                                                        userEmail.setText(commentInfos.get(position).getHost().getEmail());
                                                        userGender.setText(commentInfos.get(position).getHost().getGender());
                                                        Button update = (Button) dialog.findViewById(R.id.updateprofile);
                                                        if (commentInfos.get(i).getHost().getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                                            update.setVisibility(View.VISIBLE);
                                                        }
                                                        update.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                mContext.startActivity(new Intent(mContext, UpdateProfile.class));
                                                            }
                                                        });

                                                        try {
                                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                                            storageReference.child("Photos").child(commentInfos.get(position).getHost().getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    Picasso.with(dialog.getContext()).load(uri)
                                                                            .resize(200, 200).centerCrop().transform(new CircleTransform()).into(userProfile);
                                                                }
                                                            });
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        dialog.show();
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
                dialog.show();

            }
        });

        return v;
    }

    private void checkNetwork() {
        if (!IsNetworkAvailable()) {
            AlertDialog.Builder CheckBuilder = new AlertDialog.Builder(mContext);
            CheckBuilder.setCancelable(false);
            CheckBuilder.setTitle("Error!");
            CheckBuilder.setMessage("Check Your Internet Connection!");
            CheckBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            Toast.makeText(mContext, "Enable Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog alert = CheckBuilder.create();
            alert.show();
        } else {
            if (IsNetworkAvailable()) {
                //Toast.makeText(this, "Internet Available", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean IsNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private ArrayList<CommentInfo> fetchComments(DataSnapshot dataSnapshot) {
        final ArrayList<CommentInfo> commentInfos = new ArrayList<>();

        for (DataSnapshot data : dataSnapshot.getChildren()) {
            CommentInfo c = data.getValue(CommentInfo.class);
            commentInfos.add(0, c);
        }
        return commentInfos;
    }

    private ArrayList<CommentInfo> fetchSecretComments(DataSnapshot dataSnapshot, userInfo user, userInfo host) {
        final ArrayList<CommentInfo> commentInfos = new ArrayList<>();

        for (DataSnapshot data : dataSnapshot.getChildren()) {
            CommentInfo c = data.getValue(CommentInfo.class);
            if (c.getHost().getEmail().equals(user.getEmail()) || c.getHost().getEmail().equals(host.getEmail())) {
                commentInfos.add(0, c);
            }
        }
        return commentInfos;
    }

    private CommentListAdapter makeAdapter(Context context, ArrayList<CommentInfo> commentInfos) {
        return new CommentListAdapter(context, commentInfos);
    }
}
