package com.android.meetingbridge;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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


            databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final GroupInfo currentGroup = getCurrentGroup(dataSnapshot, id);
                    final ArrayList<userInfo> users = currentGroup.getMembersList();

                    UserListAdapter userListAdapter = new UserListAdapter(getActivity(), users);
                    membersLV.setAdapter(userListAdapter);
                    membersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final Dialog dialog = new Dialog(getActivity());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.user_profile_layout);

                            final ImageView userProfile = (ImageView) dialog.findViewById(R.id.profileImage);
                            TextView userName = (TextView) dialog.findViewById(R.id.profileName);
                            TextView userEmail = (TextView) dialog.findViewById(R.id.profileEmail);
                            TextView userContact = (TextView) dialog.findViewById(R.id.profileContact);
                            TextView userGender = (TextView) dialog.findViewById(R.id.profileGender);
                            userName.setText(users.get(position).getName());
                            userContact.setText(users.get(position).getContactNum());
                            userEmail.setText(users.get(position).getEmail());
                            userGender.setText(users.get(position).getGender());
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            storageReference.child("Photos").child(users.get(position).getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.with(dialog.getContext()).load(uri)
                                            .resize(200, 200).centerCrop().transform(new CircleTransform()).into(userProfile);
                                }
                            });
                            dialog.show();
                        }
                    });
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
                                            UserListAdapter userListAdapter = new UserListAdapter(getActivity(), users1);
                                            membersLV.setAdapter(userListAdapter);

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
