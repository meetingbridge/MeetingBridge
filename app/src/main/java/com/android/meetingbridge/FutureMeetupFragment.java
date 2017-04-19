package com.android.meetingbridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Date;

public class FutureMeetupFragment extends Fragment {


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<GroupInfo> groupInfos1 = new ArrayList<>();


    public FutureMeetupFragment() {
        // Required empty public constructor
    }

    public static FutureMeetupFragment newInstance(String id) {
        FutureMeetupFragment dis = new FutureMeetupFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        dis.setArguments(bundle);
        return dis;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_futuremeetup, container, false);
        final ListView postListView = (ListView) rootView.findViewById(R.id.postsListView);

        postListView.setEmptyView(rootView.findViewById(R.id.emptyElement));

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            final String id = bundle.getString("id");

            databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    groupInfos1 = getUserGroups(dataSnapshot);
                    int a = Integer.parseInt(id);
                    final String groupId = groupInfos1.get(a).getGroupId();

                    databaseReference.child("Posts").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final ArrayList<PostInfo> postInfo = showPosts(dataSnapshot);
                            final ArrayList<PostInfo> posts = new ArrayList<>();
                            for (int i = 0; i < postInfo.size(); i++) {
                                if (postInfo.get(i).getGroupInfo().getGroupId().equals(groupId)) {
                                    posts.add(0, postInfo.get(i));
                                }
                            }
                            if (posts.size() > 0) {
                                PostListAdapter adapter = new PostListAdapter(getActivity(), posts);
                                postListView.setAdapter(adapter);
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
            Button createPost = (Button) rootView.findViewById(R.id.createPostButton);
            createPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("type", "1");
                    startActivity(intent);
                }
            });
            Button createSecretPost = (Button) rootView.findViewById(R.id.createSecretPostButton);
            createSecretPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("type", "2");
                    startActivity(intent);
                }
            });
        }
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

    private ArrayList<GroupInfo> getUserGroups(DataSnapshot dataSnapshot) {
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
