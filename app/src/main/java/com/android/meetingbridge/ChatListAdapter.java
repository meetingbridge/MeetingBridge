package com.android.meetingbridge;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ChatInfo> mChatList;

    public ChatListAdapter(Context mContext, ArrayList<ChatInfo> mChatList) {
        this.mContext = mContext;
        this.mChatList = mChatList;
    }

    @Override
    public int getCount() {
        return mChatList.size();
    }

    @Override
    public Object getItem(int i) {
        return mChatList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        final View v = View.inflate(mContext, R.layout.chat_layout, null);
        final ImageView hostIcon = (ImageView) v.findViewById(R.id.hostIcon);
        TextView message = (TextView) v.findViewById(R.id.message);
        TextView messageTime = (TextView) v.findViewById(R.id.messageTime);
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    storageReference.child("Photos").child(mChatList.get(i).getHost().getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(v.getContext()).load(uri)
                                    .resize(200, 200).centerCrop().transform(new CircleTransform()).into(hostIcon);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        message.setText(mChatList.get(i).getMessage());
        messageTime.setText(mChatList.get(i).getPostingTime());
        hostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.user_profile_layout);
                //dialog.setTitle(users.get(position).getName());

                final ImageView userProfile = (ImageView) dialog.findViewById(R.id.profileImage);
                TextView userName = (TextView) dialog.findViewById(R.id.profileName);
                TextView userEmail = (TextView) dialog.findViewById(R.id.profileEmail);
                TextView userContact = (TextView) dialog.findViewById(R.id.profileContact);
                TextView userGender = (TextView) dialog.findViewById(R.id.profileGender);
                userName.setText(mChatList.get(i).getHost().getName());
                userContact.setText(mChatList.get(i).getHost().getContactNum());
                userEmail.setText(mChatList.get(i).getHost().getEmail());
                userGender.setText(mChatList.get(i).getHost().getGender());
                Button update = (Button) dialog.findViewById(R.id.updateprofile);
                if (mChatList.get(i).getHost().getEmail().equals(FirebaseAuth.getInstance().getCurrentUser())) {
                    update.setVisibility(View.VISIBLE);
                }
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, UpdateProfile.class));
                    }
                });
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                storageReference.child("Photos").child(mChatList.get(i).getHost().getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(dialog.getContext()).load(uri)
                                .resize(200, 200).centerCrop().transform(new CircleTransform()).into(userProfile);
                    }
                });
                dialog.show();
            }
        });
        myThread.setPriority(Thread.MAX_PRIORITY);
        myThread.start();
        return v;
    }
}
