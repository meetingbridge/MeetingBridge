package com.android.meetingbridge;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private ImageView imageView;
    private TextView uNameTV, uEmailTV;
    private DrawerLayout drawer;
    private Menu subMenu;

    //nnjnjn
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkNetwork();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        subMenu = menu.addSubMenu("Groups");
        View headerView = navigationView.getHeaderView(0);


        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        uNameTV = (TextView) headerView.findViewById(R.id.uName);
        imageView = (ImageView) headerView.findViewById(R.id.profileIcon);
        uEmailTV = (TextView) headerView.findViewById(R.id.uEmail);
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        databaseReference.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final userInfo userInfo = dataSnapshot.getValue(userInfo.class);
                uNameTV.setText(userInfo.getName());
                uEmailTV.setText(userInfo.getEmail());
                uEmailTV.setText(userInfo.getEmail());
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                storageReference.child("Photos").child(userInfo.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(HomeActivity.this).load(uri)
                                .resize(200, 200).centerCrop().transform(new CircleTransform()).into(imageView);
                    }
                });
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(HomeActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.user_profile_layout);
                        //dialog.setTitle(users.get(position).getName());

                        final ImageView userProfile = (ImageView) dialog.findViewById(R.id.profileImage);
                        TextView userName = (TextView) dialog.findViewById(R.id.profileName);
                        TextView userEmail = (TextView) dialog.findViewById(R.id.profileEmail);
                        TextView userContact = (TextView) dialog.findViewById(R.id.profileContact);
                        TextView userGender = (TextView) dialog.findViewById(R.id.profileGender);
                        userName.setText(userInfo.getName());
                        userContact.setText(userInfo.getContactNum());
                        userEmail.setText(userInfo.getEmail());
                        userGender.setText(userInfo.getGender());
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        storageReference.child("Photos").child(userInfo.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.with(dialog.getContext()).load(uri)
                                        .resize(200, 200).centerCrop().transform(new CircleTransform()).into(userProfile);
                            }
                        });
                        dialog.show();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> groupIds = showGroups(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<String> showGroups(DataSnapshot dataSnapshot) {
        ArrayList<String> groupNames = new ArrayList<>();
        ArrayList<String> groupIds = new ArrayList<>();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            GroupInfo g = data.getValue(GroupInfo.class);
            ArrayList<userInfo> users = g.getMembersList();

            for (int i = 0; i < users.size(); i++) {
                if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(users.get(i).getEmail())) {
                    groupNames.add(g.getGroupName());
                    groupIds.add(g.getGroupId());
                }
            }
        }
        subMenu.clear();
        for (int i = 0; i < groupNames.size(); i++) {
            subMenu.add(i, i, i, groupNames.get(i));
        }
        return groupIds;
    }

    private void checkNetwork() {
        if (!IsNetworkAvailable()) {
            AlertDialog.Builder CheckBuilder = new AlertDialog.Builder(this);
            CheckBuilder.setTitle("Error!");
            CheckBuilder.setMessage("Check Your Internet Connection!");

            CheckBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
            CheckBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            AlertDialog alert = CheckBuilder.create();
            alert.show();
        } else {
            if (IsNetworkAvailable()) {
                Toast.makeText(this, "Internet Available", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean IsNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
            finish();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.signout) {
            auth.signOut();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.create_group) {
            startActivity(new Intent(HomeActivity.this, CreateGroupActivity.class));

        }
        for (int i = 0; i < subMenu.size(); i++) {
            if (id == i) {
                Intent intent = new Intent(HomeActivity.this, GroupActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a GroupFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return PrivatePostFragment.newInstance();
                case 1:
                    return PublicPostFragment.newInstance();
            }
            return null;
        }


        @Override
        public int getCount() {

            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Private Posts";
                case 1:
                    return "Public Meetups";

            }
            return null;
        }
    }
}
