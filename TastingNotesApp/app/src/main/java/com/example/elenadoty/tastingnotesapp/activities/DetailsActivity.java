package com.example.elenadoty.tastingnotesapp.activities;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.elenadoty.tastingnotesapp.R;
import com.example.elenadoty.tastingnotesapp.data.BaseEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.security.AccessController.getContext;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final FloatingActionsMenu menuMultipleActions =
                (FloatingActionsMenu) findViewById(R.id.multiple_actions);


        //gone if not current user
        final FloatingActionButton removeAction = (FloatingActionButton) findViewById(R.id.button_remove);
        removeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("posts").child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid()).child(getIntent()
                        .getStringExtra("entry_id")).removeValue();

                finish();
            }
        });


        final FloatingActionButton doneAction = (FloatingActionButton) findViewById(R.id.button_gone);
        doneAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        if(getIntent().hasExtra("entry_id")){

            //Log.d("deletestuff", "current user is: " + FirebaseAuth.getInstance()
              //      .getCurrentUser().getUid());

            final String entryID = getIntent().getStringExtra("entry_id");
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference currentUserPostsReference = database.getReference("posts");
                    //.child(userID);
            currentUserPostsReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) { //
                        BaseEntry currentEntry = eventSnapshot.getValue(BaseEntry.class);
                        if(currentEntry.getDatabaseID().equals(entryID)){
                            //BaseEntry currentEntry = newEntry;
                            if(currentEntry == null) return;

                            fillInStuff(currentEntry);
                            //Log.d("deletestuff", currentEntry.getUserID());
                            //Log.d("deletestuff", FirebaseAuth.getInstance()
                              //      .getCurrentUser().getUid());

                            if(!currentEntry.getUserID().equals(FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid())){
                                ((FloatingActionsMenu) findViewById(R.id.multiple_actions))
                                        .removeButton(removeAction);
                            }
                        }
                    }
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
    }

    private void fillInStuff(BaseEntry currentEntry){

        ((TextView)findViewById(R.id.tvFragmentName))
                    .setText(currentEntry.getNoteName());
        ((TextView)findViewById(R.id.tvFragmentCategory))
                    .setText(currentEntry.getNoteCategory());
        ((TextView)findViewById(R.id.tvFragmentDateLocation))
                .setText(currentEntry.getNoteDate().toString() + ", "
                        + currentEntry.getPlaceName());

        ((TextView)findViewById(R.id.tvFragmentBody))
                .setText(currentEntry.getNoteNotes());

        ((TextView)findViewById(R.id.tvFragmentBody))
                .setMovementMethod(new ScrollingMovementMethod());

        int rating = currentEntry.getNoteRating();

        switch (rating){
            case 1:
                ((ImageView)findViewById(R.id.star2)).setImageResource(R.drawable.star_icon_unfilled);
            case 2:
                ((ImageView)findViewById(R.id.star3)).setImageResource(R.drawable.star_icon_unfilled);
            case 3:
                ((ImageView)findViewById(R.id.star4)).setImageResource(R.drawable.star_icon_unfilled);
            case 4:
                ((ImageView)findViewById(R.id.star5)).setImageResource(R.drawable.star_icon_unfilled);
            case 5:
                break;
        }

        ImageView detailedImage = findViewById(R.id.imageViewDetail);
        String imageURL = getIntent().getStringExtra("image_url");
        Glide.with(this).load(imageURL).into(detailedImage);

    }

}
