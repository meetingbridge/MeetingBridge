package com.cs.meetingbridge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MembersFragment extends Fragment {


    public MembersFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MembersFragment newInstance(String id) {
        MembersFragment fragment = new MembersFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_members, container, false);
        final Button addMemberssButton = (Button) rootView.findViewById(R.id.addMembersButton);
        final EditText emailET = (EditText) rootView.findViewById(R.id.memberEmail);
        final ListView membersLV = (ListView) rootView.findViewById(R.id.usersListView);
        final Button add = (Button) rootView.findViewById(R.id.addMembers);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        addMemberssButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailET.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
                addMemberssButton.setVisibility(View.GONE);

            }
        });
        Bundle bundle = this.getArguments();
        if (bundle != null) {

            final int id = Integer.parseInt(bundle.getString("id"));


            databaseReference.child("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final GroupInfo currentGroup = getCurrentGroup(dataSnapshot, id);
                    final ArrayList<userInfo> users = currentGroup.getMembersList();
                    ArrayList<String> userNames = new ArrayList<>();
                    for (int i = 0; i < users.size(); i++) {
                        userNames.add(users.get(i).getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userNames);
                    membersLV.setAdapter(adapter);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String email = emailET.getText().toString();
                            databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot1) {
                                    final ArrayList<userInfo> newUsers = addUsersInGroup(dataSnapshot1, email, users);
                                    databaseReference.child("Groups").child(currentGroup.getGroupId()).child("membersList").setValue(newUsers).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            emailET.setVisibility(View.GONE);
                                            add.setVisibility(View.GONE);
                                            addMemberssButton.setVisibility(View.VISIBLE);
                                            ArrayList<userInfo> users1 = currentGroup.getMembersList();
                                            ArrayList<String> userNames1 = new ArrayList<>();
                                            for (int i = 0; i < users1.size(); i++) {
                                                userNames1.add(users1.get(i).getName());
                                            }
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userNames1);
                                            membersLV.setAdapter(adapter);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return rootView;
    }

    private ArrayList<userInfo> addUsersInGroup(DataSnapshot dataSnapshot, String emailET, ArrayList<userInfo> users) {
        boolean userExist = false;
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            userInfo u = data.getValue(userInfo.class);
            String email = u.getEmail();
            if (emailET.equals(email) && !searchArray(emailET, users)) {
                users.add(u);
                userExist = true;
            }
        }
        if (searchArray(emailET, users)) {
            Toast.makeText(getActivity(), "User of this Email is added!", Toast.LENGTH_SHORT).show();
        } else if (!userExist) {
            Toast.makeText(getActivity(), "No User of this Email!", Toast.LENGTH_SHORT).show();
        }
        return users;
    }

    private boolean searchArray(String email, ArrayList<userInfo> users) {
        for (int i = 0; i < users.size(); i++) {
            if (email.equals(users.get(i).getEmail())) {
                return true;
            }
        }
        return false;
    }

    private GroupInfo getCurrentGroup(DataSnapshot dataSnapshot, int id) {
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

        GroupInfo currentGroup = groupInfos.get(id);

        return currentGroup;
    }
}
