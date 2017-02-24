package com.android.meetingbridge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PublicPostFragment extends Fragment {

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
        View rootView = inflater.inflate(R.layout.fragment_public_post, container, false);

        return rootView;
    }

}