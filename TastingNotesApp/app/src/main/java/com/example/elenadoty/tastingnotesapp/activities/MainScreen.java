package com.example.elenadoty.tastingnotesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.elenadoty.tastingnotesapp.R;
import com.example.elenadoty.tastingnotesapp.adapter.NoteAdapter;
import com.example.elenadoty.tastingnotesapp.data.BaseEntry;
import com.example.elenadoty.tastingnotesapp.data.Coordinates;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class MainScreen extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tvWelcome;
    private GoogleMap mMap;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    DatabaseReference locationReference;
    ArrayList<LatLng> markers = new ArrayList<>();
    private NoteAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(MainScreen.this, LoginActivity.class));
            finish();
        }
        else {
            tvWelcome.setText(getString(R.string.welcome, currentUser.getDisplayName()));
        }

        createRecyclerView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initMarkerListener();
        initUserPostListener();

    }

    private void createRecyclerView() {
        adapter = new NoteAdapter(this, FirebaseAuth.getInstance()
                .getCurrentUser().getUid());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerEntries);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initUserPostListener(){
        DatabaseReference currentUserPostsReference = database.getReference("posts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentUserPostsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                BaseEntry newEntry = dataSnapshot.getValue(BaseEntry.class);
                adapter.addNote(newEntry, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initMarkerListener(){
        //TODO use uid not display name
        locationReference = database.getReference("locations").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Attach a listener to read the data at our posts reference
        locationReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Coordinates baseEntry = dataSnapshot.getValue(Coordinates.class);
                LatLng location = new LatLng(baseEntry.getLatitude(),
                        baseEntry.getLongitude());
                markers.add(location);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.removePostByKey(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainScreen.this, LoginActivity.class));
                break;
            case R.id.action_add_note:
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(userID == null) break;

                Intent addIntent = new Intent(MainScreen.this, AddNoteActivity.class);
                addIntent.putExtra("userID", userID);
                startActivity(addIntent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        findViewById(R.id.postsRelativeLayout).setVisibility(View.GONE);
        findViewById(R.id.forumRelativeLayout).setVisibility(View.GONE);
        findViewById(R.id.include_map).setVisibility(View.VISIBLE);

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        for (LatLng location: markers) {
            mMap.addMarker(new MarkerOptions().position(location));
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    findViewById(R.id.postsRelativeLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.forumRelativeLayout).setVisibility(View.GONE);
                    findViewById(R.id.include_map).setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MainScreen.this);
                    return true;
                case R.id.navigation_notifications:
                    findViewById(R.id.postsRelativeLayout).setVisibility(View.GONE);
                    findViewById(R.id.forumRelativeLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.include_map).setVisibility(View.GONE);
                    return true;
            }
            return false;
        }

    };
}
