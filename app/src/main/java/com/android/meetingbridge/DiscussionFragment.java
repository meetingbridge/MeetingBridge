package com.android.meetingbridge;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiscussionFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<GroupInfo> groupInfos1 = new ArrayList<>();
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser currentUser;

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_discussion, container, false);
        final ListView postListView = (ListView) rootView.findViewById(R.id.postsListView);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        buildGoogleApiClient();

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            final String id = bundle.getString("id");

            databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    groupInfos1 = getCurrentGroup(dataSnapshot);
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        databaseReference.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final userInfo user = dataSnapshot.getValue(userInfo.class);
                databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {
                        final ArrayList<GroupInfo> groupInfos = getCurrentGroup(dataSnapshot1);

                        Bundle bundle = getArguments();
                        if (bundle != null) {

                            final String id = bundle.getString("id");
                            final GroupInfo g = groupInfos.get(Integer.parseInt(id));

                            try {
                                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                                boolean gps_enabled = false;
                                boolean network_enabled = false;

                                try {
                                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                                try {
                                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                if (!gps_enabled && !network_enabled) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                    dialog.setMessage("Location Services not enabled!");
                                    dialog.setPositiveButton("Enable Location Services", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivity(myIntent);
                                            //get gps
                                        }
                                    });
                                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                            // TODO Auto-generated method stub

                                        }
                                    });
                                    dialog.show();
                                } else {

                                    mLocationRequest = LocationRequest.create();
                                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                    mLocationRequest.setInterval(10000);
                                    if (ContextCompat.checkSelfPermission(getActivity(),
                                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                                            @Override
                                            public void onLocationChanged(Location location) {

                                                userInfo user_Info = new userInfo(currentUser.getUid(), user.getName(), user.getContactNum(),
                                                        user.getGender(), user.getEmail(), location.getLatitude(), location.getLongitude());
                                                for (int i = 0; i < g.getMembersList().size(); i++) {
                                                    if (currentUser.getEmail().equals(g.getMembersList().get(i).getEmail())) {
                                                        g.getMembersList().get(i).setLat(location.getLatitude());
                                                        g.getMembersList().get(i).setLng(location.getLongitude());
                                                    }
                                                }
                                                databaseReference.child("Groups").child(g.getGroupId()).setValue(g);
                                                databaseReference.child("Users").child(currentUser.getUid()).setValue(user_Info);

                                            }

                                        });
                                    }//
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
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
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private ArrayList<PostInfo> showPosts(DataSnapshot dataSnapshot) {
        ArrayList<PostInfo> postInfo = new ArrayList<>();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            PostInfo p = data.getValue(PostInfo.class);
            postInfo.add(p);
        }
        return postInfo;
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
