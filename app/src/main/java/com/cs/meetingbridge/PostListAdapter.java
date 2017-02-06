package com.cs.meetingbridge;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PostInfo> mPostList;

    public PostListAdapter(Context mContext, ArrayList<PostInfo> mPostList) {
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
        ImageView postIcon = (ImageView) v.findViewById(R.id.postIcon);
        TextView Title = (TextView) v.findViewById(R.id.postTitle);
        TextView groupName = (TextView) v.findViewById(R.id.groupName);
        TextView Discription = (TextView) v.findViewById(R.id.postDiscription);
        TextView TimeView = (TextView) v.findViewById(R.id.Time);
        TextView DateView = (TextView) v.findViewById(R.id.Date);

        Picasso.with(v.getContext())
                .load(Uri.parse(mPostList.get(i).getHost().getImageUri()))
                .resize(200, 200).centerCrop().into(postIcon);

        Name.setText(mPostList.get(i).getHost().getName());
        Title.setText(mPostList.get(i).getPostTitle());
        Discription.setText(mPostList.get(i).getPostDescription());
        TimeView.setText(timeString);
        DateView.setText(dateString);
        postingTime.setText(mPostList.get(i).getPostingTime());
        v.setTag(mPostList.get(i).getPostId());
        String str = " posted in " + mPostList.get(i).getGroupInfo().getGroupName();
        groupName.setText(str);

        if (mContext.getClass().toString().equals("class com.cs.meetingbridge.MainActivity")) {
            groupName.setVisibility(View.VISIBLE);
        }

        Button commentButton = (Button) v.findViewById(R.id.commentsButton);


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(v.getContext(), mPostList.get(i).getGroupInfo().getGroupId(), Toast.LENGTH_SHORT).show();
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
                                        final ArrayList<CommentInfo> commentInfos = showComments(dataSnapshot);
                                        final CommentListAdapter adapter = new CommentListAdapter(v.getContext(), commentInfos);
                                        commentLV.setAdapter(adapter);
                                        postCommentButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                commentInfos.add(0, new CommentInfo(currentTime, user, commentBox.getText().toString(), mPostList.get(i).getGroupInfo()));
                                                databaseReference.child("Comments")
                                                        .child(mPostList.get(i).getGroupInfo().getGroupId())
                                                        .child(mPostList.get(i).getPostId()).setValue(commentInfos);
                                                commentBox.setText("");
                                                commentLV.setAdapter(adapter);

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

    private ArrayList<CommentInfo> showComments(DataSnapshot dataSnapshot) {
        ArrayList<CommentInfo> commentInfo = new ArrayList<>();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            CommentInfo c = data.getValue(CommentInfo.class);
            commentInfo.add(c);
        }
        return commentInfo;
    }
}
