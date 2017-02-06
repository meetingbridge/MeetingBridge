package com.cs.meetingbridge;

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


public class CommentListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CommentInfo> mCommentList;

    public CommentListAdapter(Context mContext, ArrayList<CommentInfo> mCommentList) {
        this.mContext = mContext;
        this.mCommentList = mCommentList;
    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    @Override
    public Object getItem(int i) {
        return mCommentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public View getView(final int i, View view, ViewGroup viewGroup) {
        final View v = View.inflate(mContext, R.layout.commentlayout, null);
        TextView commentTime = (TextView) v.findViewById(R.id.commentTimeTV);
        TextView commentHost = (TextView) v.findViewById(R.id.hostNameTV);
        TextView commentDiscription = (TextView) v.findViewById(R.id.commentDescriptionTV);
        commentHost.setText(mCommentList.get(i).getHost().getName());
        commentDiscription.setText(mCommentList.get(i).getCommentDescription());
        commentTime.setText(mCommentList.get(i).getCommentTime());
        v.setTag(mCommentList.get(i).getCommentDescription());
        final ImageView hostIcon = (ImageView) v.findViewById(R.id.hostIcon);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("Photos").child(mCommentList.get(i).getHost().getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(v.getContext()).load(uri)
                        .resize(200, 200).centerCrop().into(hostIcon);
            }
        });

        System.out.println(mCommentList.get(i).getHost().getImageUri());

        return v;
    }
}
