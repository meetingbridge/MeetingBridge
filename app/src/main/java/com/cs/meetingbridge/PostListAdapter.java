package com.cs.meetingbridge;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(mContext, R.layout.postlayout, null);
        PostTime time = mPostList.get(i).getPostTime();
        PostDate date = mPostList.get(i).getPostDate();
        String timeString = String.valueOf(time.getHours()) + ":" + String.valueOf(time.getMinutes()) + " " + time.getAmpm();
        String dateString = String.valueOf(date.getDay()) + "/" + String.valueOf(date.getMonth()) + "/" + String.valueOf(date.getYear());
        TextView Name = (TextView) v.findViewById(R.id.hostName);
        ImageView postIcon = (ImageView) v.findViewById(R.id.postIcon);
        TextView Title = (TextView) v.findViewById(R.id.postTitle);
        TextView Discription = (TextView) v.findViewById(R.id.postDiscription);
        TextView TimeView = (TextView) v.findViewById(R.id.Time);
        TextView DateView = (TextView) v.findViewById(R.id.Date);
        Name.setText(mPostList.get(i).getHost().getName());
        Title.setText(mPostList.get(i).getPostTitle());
        Discription.setText(mPostList.get(i).getPostDescription());
        TimeView.setText(timeString);
        DateView.setText(dateString);
        v.setTag(mPostList.get(i).getPostId());

        return v;
    }
}
