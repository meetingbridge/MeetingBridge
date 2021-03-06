package com.android.meetingbridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PublicPostFragment extends Fragment {

    public static Boolean check = true;

    public PublicPostFragment() {
        // Required empty public constructor
    }

    public static PublicPostFragment newInstance() {
        PublicPostFragment fragment = new PublicPostFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.fragment_public_post, container, false);
        Button button = (Button) rootView.findViewById(R.id.newPublicMeetup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreatePublicPostActivity.class));
            }
        });

        final ListView postListView = (ListView) rootView.findViewById(R.id.postListView);
        postListView.setEmptyView(rootView.findViewById(R.id.emptyElement));

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("PublicPosts").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final ArrayList<PostInfo> temp = new ArrayList<>();
                ArrayList<PostInfo> postInfos = showPosts(dataSnapshot);

                for (int i = 0; i < postInfos.size(); i++) {
                    if (!searchArray(postInfos.get(i).getPostId(), temp)) {
                        temp.add(0, postInfos.get(i));
                    }
                }


                if (temp.size() > 0) {
                    Collections.sort(temp, new Comparator<PostInfo>() {
                        @Override
                        public int compare(PostInfo lhs, PostInfo rhs) {
                            return rhs.getPostingTime().compareTo(lhs.getPostingTime());
                        }
                    });

                    PublicPostListAdapter adapter = new PublicPostListAdapter(getActivity(), temp);
                    postListView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    public ArrayList<PostInfo> showPosts(DataSnapshot dataSnapshot) {
        ArrayList<PostInfo> postInfos = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            PostInfo postInfo = ds.getValue(PostInfo.class);
            postInfos.add(0, postInfo);
        }
        return postInfos;
    }

    private boolean searchArray(String postId, ArrayList<PostInfo> postInfos) {
        for (int i = 0; i < postInfos.size(); i++) {
            if (postId.equals(postInfos.get(i).getPostId())) {
                return true;
            }
        }
        return false;
    }

}