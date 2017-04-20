package com.android.meetingbridge;


import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<userInfo> userList;

    public UserListAdapter(Context mContext, ArrayList<userInfo> userList) {
        this.userList = userList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int i, View view, ViewGroup parent) {
        final View v = View.inflate(mContext, R.layout.userinfo_layout, null);
        TextView userName = (TextView) v.findViewById(R.id.userName);
        TextView userEmail = (TextView) v.findViewById(R.id.userEmail);
        final ImageView userIcon = (ImageView) v.findViewById(R.id.userIcon);
        userName.setText(userList.get(i).getName());
        userEmail.setText(userList.get(i).getEmail());
        Thread my = new Thread(new Runnable() {
            @Override
            public void run() {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                storageReference.child("Photos").child(userList.get(i).getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(v.getContext())
                                .load(uri)
                                .resize(200, 200).centerCrop()
                                .transform(new CircleTransform()).into(userIcon);
                    }
                });
            }
        });
        my.setPriority(Thread.MAX_PRIORITY);
        my.start();
        v.setTag(userList.get(i).getId());
        return v;
    }
}
