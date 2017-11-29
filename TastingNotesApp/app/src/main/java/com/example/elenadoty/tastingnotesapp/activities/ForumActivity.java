package com.example.elenadoty.tastingnotesapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.elenadoty.tastingnotesapp.R;
import com.example.elenadoty.tastingnotesapp.adapter.NoteAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class ForumActivity extends AppCompatActivity {

    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
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
}
