package com.example.elenadoty.tastingnotesapp.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.elenadoty.tastingnotesapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.security.AccessController.getContext;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fragmentFabDelete);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete from firebase after ensuring
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("posts").child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid()).child(getIntent()
                        .getStringExtra("entry_id")).removeValue();
                finish();
            }
        });

        FloatingActionButton fabDone = (FloatingActionButton) findViewById(R.id.fragmentFabDone);
        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(getIntent().hasExtra("entry_name")){

            ((TextView)findViewById(R.id.tvFragmentName))
                    .setText(getIntent().getStringExtra("entry_name"));
            ((TextView)findViewById(R.id.tvFragmentDateLocation))
                    .setText(getIntent().getStringExtra("entry_date") + ", "
                            + getIntent().getStringExtra("entry_location"));

            ((TextView)findViewById(R.id.tvFragmentBody))
                    .setText(getIntent().getStringExtra("entry_body"));

            ((TextView)findViewById(R.id.tvFragmentBody))
                    .setMovementMethod(new ScrollingMovementMethod());

            ImageView detailedImage = findViewById(R.id.imageViewDetail);
            String imageURL = getIntent().getStringExtra("image_url");
            Glide.with(this).load(imageURL).into(detailedImage);


            int rating = getIntent().getIntExtra("entry_rating", 1);

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

        }
    }

}
