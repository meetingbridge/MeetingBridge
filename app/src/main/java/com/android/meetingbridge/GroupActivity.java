package com.android.meetingbridge;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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

public class GroupActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String ABC;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DrawerLayout drawer;
    private Menu subMenu;
    private ImageView imageView;
    private TextView uNameTV, uEmailTV;
    private FloatingActionButton fab;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private Place destination;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private LocationRequest mLocationRequest;
    private FirebaseUser currentUser;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        auth = FirebaseAuth.getInstance();
        buildGoogleApiClient();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (currentUser == null) {
                    startActivity(new Intent(GroupActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_group);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getNavPopulated(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPlaceAutocompleteActivityIntent();
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 2) {
                    fab.show();
                    Toast.makeText(getApplicationContext(), "Tap Direction Button", Toast.LENGTH_SHORT).show();
                } else {
                    fab.hide();
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(this);
        subMenu = navigationView.getMenu().addSubMenu("Groups");
        MenuItem homeItem = navigationView.getMenu().findItem(R.id.homeActivity);
        homeItem.setVisible(true);
        View headerView = navigationView.getHeaderView(0);
        uNameTV = (TextView) headerView.findViewById(R.id.uName);
        imageView = (ImageView) headerView.findViewById(R.id.profileIcon);
        uEmailTV = (TextView) headerView.findViewById(R.id.uEmail);
        databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
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
                        Picasso.with(GroupActivity.this).load(uri)
                                .resize(200, 200).centerCrop().transform(new CircleTransform()).into(imageView);
                    }
                });
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(GroupActivity.this);
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
                Toast.makeText(GroupActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
                        final GroupInfo g = getCurrentGroup(dataSnapshot1);

                        try {

                            mLocationRequest = LocationRequest.create();
                            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            mLocationRequest.setInterval(10000);
                            if (ContextCompat.checkSelfPermission(getApplicationContext(),
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
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                destination = PlaceAutocomplete.getPlace(this, data);
                databaseReference.child("Groups").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GroupInfo g = getCurrentGroup(dataSnapshot);
                        ArrayList<LocationInfo> locationInfos = new ArrayList<>();
                        for (int i = 0; i < g.getMembersList().size(); i++) {
                            LocationInfo locationInfo = new LocationInfo(g.getMembersList().get(i), destination);
                            locationInfos.add(0, locationInfo);
                        }
                        LocationListAdapter locationListAdapter = new LocationListAdapter(getApplicationContext(), locationInfos);

                        Dialog dialog = new Dialog(GroupActivity.this);
                        dialog.setContentView(R.layout.locationlistlayout);

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.VISIBLE);
                        dialog.setTitle(g.getGroupName());
                        ListView listView = (ListView) dialog.findViewById(R.id.locList);
                        listView.setAdapter(locationListAdapter);
                        dialog.show();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                String str = destination.getName().toString() + " " + destination.getAddress().toString();
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
            } else if (requestCode == RESULT_CANCELED) {

            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.signout) {
            auth.signOut();

        } else if (id == R.id.homeActivity) {
            Intent intent = new Intent(GroupActivity.this, HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.create_group) {
            startActivity(new Intent(GroupActivity.this, CreateGroupActivity.class));

        }
        for (int i = 0; i < subMenu.size(); i++) {
            if (id == i) {
                Intent intent = new Intent(GroupActivity.this, GroupActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            startActivity(new Intent(GroupActivity.this, HomeActivity.class));
            finish();
        }
    }

    private GroupInfo getCurrentGroup(DataSnapshot dataSnapshot) {
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
        String id = getIntent().getExtras().get("id").toString();
        int a = Integer.parseInt(id);
        GroupInfo g = new GroupInfo();
        if (groupInfos.size() > 0) {
            g = groupInfos.get(a);
        }
        return g;
    }

    private void getNavPopulated(DataSnapshot dataSnapshot) {
        ArrayList<GroupInfo> groupInfos = new ArrayList<>();
        ArrayList<String> groupNames = new ArrayList<>();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            GroupInfo g = data.getValue(GroupInfo.class);
            ArrayList<userInfo> userInfos = g.getMembersList();

            for (int i = 0; i < userInfos.size(); i++) {
                if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(userInfos.get(i).getEmail())) {
                    groupInfos.add(g);
                    groupNames.add(g.getGroupName());
                }
            }
        }
        for (int i = 0; i < groupNames.size(); i++) {
            subMenu.add(Menu.NONE, i, Menu.NONE, groupNames.get(i));
        }
        String id = getIntent().getExtras().get("id").toString();
        int a = Integer.parseInt(id);
        if (groupInfos.size() > 0) {
            ABC = groupInfos.get(a).getGroupName();
            setTitle(ABC);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
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

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        String id = getIntent().getExtras().get("id").toString();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a GroupFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return DiscussionFragment.newInstance(id);
                case 1:
                    return MembersFragment.newInstance(id);
                case 2:
                    return MapsFragment.newInstance(id);
            }
            return null;
        }


        @Override
        public int getCount() {

            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Discussion";
                case 1:
                    return "Members";
                case 2:
                    return "Location";
            }
            return null;
        }
    }
}
