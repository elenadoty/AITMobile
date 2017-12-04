package com.example.elenadoty.tastingnotesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.elenadoty.tastingnotesapp.R;
import com.example.elenadoty.tastingnotesapp.adapter.NoteAdapter;
import com.example.elenadoty.tastingnotesapp.data.BaseEntry;
import com.example.elenadoty.tastingnotesapp.data.Coordinates;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.navigation_home).callOnClick();

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

        for (LatLng location: markers) {
            mMap.addMarker(new MarkerOptions().position(location));
        }
        /*LatLng google = new LatLng(43.454063, -80.499093);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(google));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(google) // Sets the center of the map to
                .zoom(20)                   // Sets the zoom
                .tilt(0)    // Sets the tilt of the camera to 30 degrees
                .build();    // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                cameraPosition));*/
        //mMap.setMinZoomPreference(20);
    }

    public void addMarker(LatLng markerToAdd){
        markers.add(markerToAdd);
        updateMapMarkers();
    }

    public void removeMarker(LatLng markerToRemove){
        int toRemove = -1;
        for(int i = 0; i < markers.size(); i ++){
            if(markers.get(i).longitude == markerToRemove.longitude
                    && markers.get(i).latitude == markerToRemove.latitude){
                toRemove = i;
            }
        }
        if(toRemove > -1) {
            markers.remove(toRemove);
        }
        updateMapMarkers();
    }

    private void updateMapMarkers(){
        if(mMap != null){
            mMap.clear();
            for (LatLng location: markers) {
                mMap.addMarker(new MarkerOptions().position(location));
            }
        }
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
                    MapFragment mapFragment = (MapFragment) getFragmentManager()
                            .findFragmentById(R.id.include_map);
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
