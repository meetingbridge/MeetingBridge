package com.android.meetingbridge;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<LocationInfo> locationList;

    public LocationListAdapter(Context mContext, ArrayList<LocationInfo> LocationList) {
        this.locationList = LocationList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return locationList.size();
    }

    @Override
    public Object getItem(int position) {
        return locationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        final View v = View.inflate(mContext, R.layout.location_info_layout, null);
        TextView userName = (TextView) v.findViewById(R.id.userName);
        TextView userloc = (TextView) v.findViewById(R.id.userLoc);
        TextView destination = (TextView) v.findViewById(R.id.dest);
        TextView time = (TextView) v.findViewById(R.id.reqTime);
        final ImageView userIcon = (ImageView) v.findViewById(R.id.userIcon);
        userName.setText(locationList.get(i).getName());
        userloc.setText(getAddress(locationList.get(i).getLat(), locationList.get(i).getLng()));
        String dest = String.valueOf(locationList.get(i).getDest().getName()) + ", " + String.valueOf(locationList.get(i).getDest().getAddress());
        destination.setText(dest);
        String t = String.valueOf(distance(locationList.get(i).getLat(), locationList.get(i).getLng(),
                locationList.get(i).getDest().getLatLng().latitude, locationList.get(i).getDest().getLatLng().longitude, "K")) + " KM away from destination";
        time.setText(t);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("Photos").child(locationList.get(i).getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(v.getContext())
                        .load(uri)
                        .resize(200, 200).centerCrop().into(userIcon);
            }
        });
        v.setTag(locationList.get(i).getId());
        return v;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }
        return Math.round(dist);
    }

    private String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getLocality() + ", " + obj.getCountryName();

            return add;

        } catch (IOException e) {
            e.printStackTrace();
            // Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
