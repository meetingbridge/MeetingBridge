package com.android.meetingbridge;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class MapsFragment extends SupportMapFragment
        implements OnMapReadyCallback {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<GroupInfo> groupInfos1 = new ArrayList<>();
    private GoogleMap mMap;

    public MapsFragment() {
    }

    public static MapsFragment newInstance(String id) {
        MapsFragment map = new MapsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        map.setArguments(bundle);
        return map;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(31.1704, 72.7097)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6.77f));

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            final String id = bundle.getString("id");

            databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    groupInfos1 = getCurrentGroup(dataSnapshot);
                    int a = Integer.parseInt(id);
                    final String groupId = groupInfos1.get(a).getGroupId();
                    databaseReference.child("Groups").child(groupId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mMap.clear();
                            GroupInfo g = dataSnapshot.getValue(GroupInfo.class);
                            final ArrayList<userInfo> membersList = g.getMembersList();
                            for (int i = 0; i < membersList.size(); i++) {
                                final int finalI = i;
                                databaseReference.child("Users").child(membersList.get(i).getId()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        userInfo u = dataSnapshot.getValue(userInfo.class);
                                        if (!searchArray(u, membersList)) {
                                            membersList.add(u);
                                        } else if (searchArray(u, membersList)) {
                                            membersList.set(finalI, u);
                                        }
                                        showMarkers(membersList);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
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
    }

    private boolean searchArray(userInfo u, ArrayList<userInfo> userInfoArrayList) {
        for (int i = 0; i < userInfoArrayList.size(); i++) {
            if (u.getEmail().equals(userInfoArrayList.get(i).getEmail())) {
                return true;
            }
        }
        return false;
    }

    private void showMarkers(ArrayList<userInfo> memberList) {


        final float[] colours = {BitmapDescriptorFactory.HUE_AZURE,
                BitmapDescriptorFactory.HUE_BLUE,
                BitmapDescriptorFactory.HUE_CYAN,
                BitmapDescriptorFactory.HUE_GREEN,
                BitmapDescriptorFactory.HUE_MAGENTA,
                BitmapDescriptorFactory.HUE_ORANGE,
                BitmapDescriptorFactory.HUE_ROSE,
                BitmapDescriptorFactory.HUE_VIOLET,
                BitmapDescriptorFactory.HUE_YELLOW};
        mMap.clear();
        for (int i = 0; i < memberList.size(); i++) {
            createMarker(memberList.get(i).getLat(), memberList.get(i).getLng(), memberList.get(i).getName(),
                    BitmapDescriptorFactory.defaultMarker(colours[new Random().nextInt(colours.length)]));
        }
    }

    private Marker createMarker(double latitude, double longitude, String
            title, BitmapDescriptor color) {
        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(color));
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