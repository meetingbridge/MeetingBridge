package com.android.meetingbridge;

import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.text.TextUtils.isEmpty;

public class ChatFragment extends Fragment {


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<GroupInfo> groupInfos1 = new ArrayList<>();


    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String id) {
        ChatFragment dis = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        dis.setArguments(bundle);
        return dis;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        final ListView chatListView = (ListView) rootView.findViewById(R.id.chatListView);
        chatListView.setEmptyView(rootView.findViewById(R.id.emptyElement));
        final Button sendMessage = (Button) rootView.findViewById(R.id.sendMessage);
        final EditText newMessage = (EditText) rootView.findViewById(R.id.message);
        final String currentTime = new SimpleDateFormat("dd-M-yy hh:mm a").format(new Date());
        Bundle bundle = this.getArguments();
        if (bundle != null) {

            String id = bundle.getString("id");
            final int a = Integer.parseInt(id);
            databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<GroupInfo> groupInfos = getUserGroups(dataSnapshot);
                    final GroupInfo currentGroup = groupInfos.get(a);
                    databaseReference.child("Chat").child(currentGroup.getGroupId()).addValueEventListener(new ValueEventListener() {
                        boolean check = true;

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<ChatInfo> chatInfos = showMesssages(dataSnapshot);
                            if (chatInfos.size() > 0) {
                                ChatInfo c = chatInfos.get(chatInfos.size() - 1);
                                if (check) {
                                    if (!c.getHost().getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                        try {
                                            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity())
                                                    .setContentTitle(c.getHost().getName() + " in " + c.getCurrentGroup().getGroupName() + ".")
                                                    .setSmallIcon(R.mipmap.ic_launcher_transparent)
                                                    .setLargeIcon(bm)
                                                    .setContentText(c.getMessage());
                                            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                                            notificationManager.notify(NotificationID.getID(), mBuilder.build());
                                            check = false;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                } else {
                                    check = true;
                                }
                                ChatListAdapter adapter = new ChatListAdapter(getActivity(), chatInfos);
                                chatListView.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final userInfo currentUser = dataSnapshot.getValue(userInfo.class);
                            sendMessage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEmpty(newMessage.getText())) {
                                        newMessage.setError("Enter Some Message to Send!");
                                        return;
                                    }
                                    ChatInfo chatInfo = new ChatInfo("1", newMessage.getText().toString(), currentTime, currentUser, currentGroup);

                                    databaseReference.child("Chat").child(currentGroup.getGroupId()).push().setValue(chatInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            databaseReference.child("Chat").child(currentGroup.getGroupId()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    addId(dataSnapshot);
                                                    newMessage.setText(null);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
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

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        return rootView;
    }

    private void addId(DataSnapshot dataSnapshot) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            ChatInfo p = data.getValue(ChatInfo.class);
            if (String.valueOf(p.getMessageId()).equals("1")) {
                String k = data.getKey();
                p.setMessageId(k);
                databaseReference.child("Chat").child(p.getCurrentGroup().getGroupId()).child(k).setValue(p);
                break;
            }

        }
    }

    private ArrayList<ChatInfo> showMesssages(DataSnapshot dataSnapshot) {
        ArrayList<ChatInfo> chatInfo = new ArrayList<>();

        for (DataSnapshot data : dataSnapshot.getChildren()) {
            ChatInfo c = data.getValue(ChatInfo.class);
            chatInfo.add(c);
        }
        return chatInfo;
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
