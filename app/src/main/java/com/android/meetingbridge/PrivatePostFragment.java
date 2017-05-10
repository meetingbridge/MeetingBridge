package com.android.meetingbridge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class PrivatePostFragment extends Fragment {

    public PrivatePostFragment() {
        // Required empty public constructor
    }

    public static PrivatePostFragment newInstance() {
        PrivatePostFragment fragment = new PrivatePostFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_private_post, container, false);

        final ListView postListView = (ListView) rootView.findViewById(R.id.postListView);

        postListView.setEmptyView(rootView.findViewById(R.id.emptyElement));
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> groupIds = showGroups(dataSnapshot);
                databaseReference.child("Posts").addValueEventListener(new ValueEventListener() {
                    boolean check = true;

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {
                        ArrayList<PostInfo> postInfos = showPosts(dataSnapshot1);
                        final ArrayList<PostInfo> temp = new ArrayList<>();
                        final PrivatePostListAdapter privatePostListAdapter = new PrivatePostListAdapter(getActivity(), temp);
                        for (int i = 0; i < groupIds.size(); i++) {
                            for (int j = 0; j < postInfos.size(); j++) {
                                if (!searchArray(postInfos.get(j).getPostId(), temp) && postInfos.get(j).getGroupInfo().getGroupId().equals(groupIds.get(i))) {
                                    temp.add(0, postInfos.get(j));
                                }
                            }
                        }

                        if (temp.size() > 0) {
                            Collections.sort(temp, new Comparator<PostInfo>() {
                                @Override
                                public int compare(PostInfo lhs, PostInfo rhs) {
                                    return rhs.getPostingTime().compareTo(lhs.getPostingTime());
                                }
                            });
                            postListView.setAdapter(privatePostListAdapter);
                        }
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
        return rootView;
    }

    private ArrayList<PostInfo> showPosts(DataSnapshot dataSnapshot) {
        ArrayList<PostInfo> postInfo = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String current = new SimpleDateFormat("dd-MM-yyyy").format(new Date());


        for (DataSnapshot data : dataSnapshot.getChildren()) {
            PostInfo p = data.getValue(PostInfo.class);
            String postDate = p.getPostDate().getDay() + "-" + p.getPostDate().getMonth() + "-" + p.getPostDate().getYear();
            try {
                Date d1 = sdf.parse(postDate);
                Date d2 = sdf.parse(current);
                if (d2.before(d1) || d2.equals(d1)) {
                    postInfo.add(p);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return postInfo;
    }

    private boolean searchArray(String posrId, ArrayList<PostInfo> postInfos) {
        for (int i = 0; i < postInfos.size(); i++) {
            if (posrId.equals(postInfos.get(i).getPostId())) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> showGroups(DataSnapshot dataSnapshot) {
        ArrayList<String> groupNames = new ArrayList<>();
        ArrayList<String> groupIds = new ArrayList<>();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            GroupInfo g = data.getValue(GroupInfo.class);
            ArrayList<userInfo> users = g.getMembersList();

            for (int i = 0; i < users.size(); i++) {
                if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(users.get(i).getEmail())) {
                    groupNames.add(g.getGroupName());
                    groupIds.add(g.getGroupId());
                }
            }
        }
        return groupIds;
    }

}
