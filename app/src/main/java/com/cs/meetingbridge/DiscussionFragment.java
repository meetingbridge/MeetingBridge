package com.cs.meetingbridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiscussionFragment extends Fragment {


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<GroupInfo> groupInfos1 = new ArrayList<>();

    public DiscussionFragment() {
        // Required empty public constructor
    }

    public static DiscussionFragment newInstance(String id) {
        DiscussionFragment dis = new DiscussionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        dis.setArguments(bundle);
        return dis;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_discussion, container, false);
        final TextView textView = (TextView) rootView.findViewById(R.id.textView);
        DiscussionFragment context = this;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            final String id = bundle.getString("id");
            databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    groupInfos1 = getCurrentGroup(dataSnapshot);
                    int a = Integer.parseInt(id);
                    String x = groupInfos1.get(a).getGroupName();
                    textView.setText(x);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        Button createPost = (Button) rootView.findViewById(R.id.createPostButton);
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreatePostActivity.class));
            }
        });
        return rootView;
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
