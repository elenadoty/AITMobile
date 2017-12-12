package com.example.elenadoty.tastingnotesapp.activities;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.elenadoty.tastingnotesapp.R;
import com.example.elenadoty.tastingnotesapp.adapter.NoteAdapter;
import com.example.elenadoty.tastingnotesapp.data.BaseEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForumActivity extends Fragment {

    private NoteAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private View rootView;

    private Spinner spCategory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_forum, container, false);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        spCategory = rootView.findViewById(R.id.spCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        ArrayAdapter<CharSequence> spinnerAdapter =
                ArrayAdapter.createFromResource(getContext(),
                        R.array.category_array, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(spinnerAdapter);

        if (spCategory.getSelectedItem() == "Breakfast") {
            //set items not labeled breakfast to gone
        }

        createRecyclerView(rootView);
        initUserAllPostListener();
        this.rootView = rootView;
        return rootView;

    }

    private void createRecyclerView(View rootView) {
        adapter = new NoteAdapter(getContext(), NoteAdapter.ALL_POSTS);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerForumEntries);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.updateBackground(rootView);
    }

    private void initUserAllPostListener(){
        DatabaseReference currentUserPostsReference = database.getReference("posts");
        currentUserPostsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                        BaseEntry newEntry = eventSnapshot.getValue(BaseEntry.class);
                        adapter.addNote(newEntry, newEntry.getDatabaseID());
                        adapter.updateBackground(rootView);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    BaseEntry newEntry = eventSnapshot.getValue(BaseEntry.class);
                    adapter.removePostByKey(newEntry.getDatabaseID());
                    adapter.addNote(newEntry, newEntry.getDatabaseID());
                    adapter.updateBackground(rootView);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    BaseEntry newEntry = eventSnapshot.getValue(BaseEntry.class);
                    adapter.removePostByKey(newEntry.getDatabaseID());
                    adapter.updateBackground(rootView);
                }
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
