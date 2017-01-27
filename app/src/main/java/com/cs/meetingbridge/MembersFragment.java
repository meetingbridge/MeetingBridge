package com.cs.meetingbridge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MembersFragment extends Fragment {


    public MembersFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MembersFragment newInstance() {
        MembersFragment fragment = new MembersFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_members, container, false);


        return rootView;
    }
}
