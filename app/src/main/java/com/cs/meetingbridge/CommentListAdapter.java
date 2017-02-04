package com.cs.meetingbridge;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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

    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(mContext, R.layout.commentlayout, null);
        TextView commentTime = (TextView) v.findViewById(R.id.commentTimeTV);
        TextView commentHost = (TextView) v.findViewById(R.id.hostNameTV);
        TextView commentDiscription = (TextView) v.findViewById(R.id.commentDescriptionTV);
        commentHost.setText(mCommentList.get(i).getHost().getName());
        commentDiscription.setText(mCommentList.get(i).getCommentDescription());
        commentTime.setText(mCommentList.get(i).getCommentTime());
        v.setTag(mCommentList.get(i).getCommentDescription());
        return v;
    }
}
