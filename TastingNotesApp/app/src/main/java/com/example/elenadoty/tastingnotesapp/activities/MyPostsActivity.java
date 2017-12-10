package com.example.elenadoty.tastingnotesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.elenadoty.tastingnotesapp.R;
import com.example.elenadoty.tastingnotesapp.adapter.NoteAdapter;
import com.example.elenadoty.tastingnotesapp.data.BaseEntry;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyPostsActivity extends Fragment {

    private NoteAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private View rootView;
    private ImageButton btnAdd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_my_posts, container, false);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        createRecyclerView(rootView);
        initUserPostListener();
        this.rootView = rootView;

        btnAdd = (ImageButton) rootView.findViewById(R.id.addNoMyPosts);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("buttonstuff", "button clicked");
                Intent intent = new Intent(getActivity(), AddNoteActivity.class);
                intent.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
            }
        });

        return rootView;

    }

    private void createRecyclerView(View rootView) {
        adapter = new NoteAdapter(getContext(), NoteAdapter.MY_POSTS);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerEntries);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.updateBackground(rootView);
    }


    private void initUserPostListener(){
        DatabaseReference currentUserPostsReference = database.getReference("posts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentUserPostsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                BaseEntry newEntry = dataSnapshot.getValue(BaseEntry.class);
                adapter.addNote(newEntry, newEntry.getDatabaseID());
                ((MainScreen)getContext()).addMapMarker(new LatLng(
                        newEntry.getCoordinates().getLatitude(),
                        newEntry.getCoordinates().getLongitude()));
                adapter.updateBackground(rootView);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                BaseEntry newEntry = dataSnapshot.getValue(BaseEntry.class);
                adapter.removePostByKey(newEntry.getDatabaseID());
                ((MainScreen)getContext()).removeMarker(new LatLng(
                        newEntry.getCoordinates().getLatitude(),
                        newEntry.getCoordinates().getLongitude()));
                adapter.updateBackground(rootView);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
