package com.cs.meetingbridge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiscussionFragment extends Fragment {


    ArrayList<GroupInfo> groupInfos = new ArrayList<>();

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
        final View rootView = inflater.inflate(R.layout.fragment_discussion, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {

            final String id = bundle.getString("id");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        GroupInfo g = data.getValue(GroupInfo.class);
                        ArrayList<userInfo> userInfos = g.getMembersList();
                        for (int i = 0; i < userInfos.size(); i++) {
                            if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(userInfos.get(i).getEmail())) {
                                groupInfos.add(g);
                            }
                        }
                    }
                    int a = Integer.parseInt(id);
                    String x = groupInfos.get(a).getGroupName();
                    TextView textView = (TextView) rootView.findViewById(R.id.textView);
                    textView.setText(x);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        return rootView;
    }
}
