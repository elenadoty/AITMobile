package com.example.elenadoty.tastingnotesapp.activities;

import android.app.DialogFragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.elenadoty.tastingnotesapp.R;
import com.example.elenadoty.tastingnotesapp.data.BaseEntry;
import com.example.elenadoty.tastingnotesapp.data.Coordinates;
import com.example.elenadoty.tastingnotesapp.data.NoteDate;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.UUID;

import quatja.com.vorolay.VoronoiView;

public class AddNoteActivity extends AppCompatActivity implements OnConnectionFailedListener{

    public static final int REQUEST_CODE_CAMERA = 101;
    DatabaseReference databaseReference;
    private GoogleApiClient mGoogleApiClient;
    final int PLACE_PICKER_REQUEST = 1001;
    Coordinates newCoords;
    static NoteDate newDate;
    static String newName;
    static int rating;
    static String noteNotes;
    static String placeName;
    static Bitmap imageAsBitmap;
    static String imageURL;

    ImageButton rating1, rating2, rating3, rating4, rating5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        if (!getIntent().hasExtra("userID")) {
            finish();
        }

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        setRatingOnClick();

        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fragmentFabDoneAdd);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newDate != null && newName != null && newCoords != null
                        && placeName != null && rating != 0 && noteNotes != null){
                    try {
                        uploadImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                } else {

                    String needed = "Need: ";
                    if(newDate == null) needed += "Date. ";
                    if(newName == null) needed += "Name. ";
                    if(newCoords == null) needed += "Location. ";
                    if(rating == 0) needed += "Rating. ";
                    if(noteNotes == null) needed += "Notes.";

                    Snackbar.make(findViewById(R.id.contentAddNote),
                            "Must enter all info. " + needed, Snackbar.LENGTH_LONG).show();

                }
            }
        });

        dealWithVoronoi();

        databaseReference = FirebaseDatabase.getInstance().getReference();


    }

    private void dealWithVoronoi() {
        VoronoiView voronoiView = (VoronoiView) findViewById(R.id.voronoi_view);
        LayoutInflater layoutInflater = getLayoutInflater();

        for (int i = 0; i < 6; i++) {
            RelativeLayout rl = (RelativeLayout) layoutInflater.inflate(R.layout.item_voronoi, null, false);
            final KenBurnsView viewToAdd = (KenBurnsView) rl.findViewById(R.id.image_voronoi);

            final int j = i;
            viewToAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (j) {
                        case 1:
                            //set calendar
                            DialogFragment newFragment = new DatePickerFragment();
                            newFragment.show(getFragmentManager(), "datePicker");
                            viewToAdd.setImageResource(R.drawable.prop_done_icon);
                            viewToAdd.setEnabled(false);
                            break;
                        case 2:
                            //ratings
                            findViewById(R.id.contentAddNote).setAlpha((float)0.1);
                            findViewById(R.id.fragmentFabDoneAdd).setAlpha((float)0.1);
                            findViewById(R.id.ratingsIncluded).setVisibility(View.VISIBLE);
                            viewToAdd.setImageResource(R.drawable.prop_done_icon);
                            viewToAdd.setEnabled(false);
                            break;
                        case 3:
                            //maps
                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                            builder.setLatLngBounds(
                                    new LatLngBounds(new LatLng(18.841825, -157.365834),
                                            new LatLng(21.000411, -154.416005)));

                            try {
                                startActivityForResult(builder.build(AddNoteActivity.this),
                                        PLACE_PICKER_REQUEST);
                            } catch (GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                            } catch (GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                            }
                            viewToAdd.setImageResource(R.drawable.prop_done_icon);
                            viewToAdd.setEnabled(false);

                            break;
                        case 4:
                            //notes
                            android.support.v4.app.DialogFragment newNoteFragment = new AddNotesDialog();
                            newNoteFragment.show(getSupportFragmentManager(), "note_fragment");
                            viewToAdd.setImageResource(R.drawable.prop_done_icon);
                            viewToAdd.setEnabled(false);

                            break;
                        case 0:
                            //name
                            android.support.v4.app.DialogFragment newNameFragment = new NameDialog();
                            newNameFragment.show(getSupportFragmentManager(), "name_fragment");
                            viewToAdd.setImageResource(R.drawable.prop_done_icon);
                            viewToAdd.setEnabled(false);

                            break;
                        case 5:
                            //camera stuff
                            requestNeededPermission();
                            viewToAdd.setImageResource(R.drawable.prop_done_icon);
                            viewToAdd.setEnabled(false);

                            break;
                        default:
                            break;
                    }
                }
            });


            switch (i) {
                case 1:
                    viewToAdd.setImageResource(R.drawable.calendar);
                    break;
                case 2:
                    viewToAdd.setImageResource(R.drawable.ratings);
                    break;
                case 3:
                    viewToAdd.setImageResource(R.drawable.mapsicon);
                    break;
                case 4:
                    viewToAdd.setImageResource(R.drawable.notes);
                    break;
                case 5:
                    viewToAdd.setImageResource(R.drawable.camera);
                    break;
                case 0:
                    viewToAdd.setImageResource(R.drawable.descriptions);
                    break;
                default:
                    break;
            }
            voronoiView.addView(rl);
        }
    }

    private void setRatingOnClick() {
        rating1 = findViewById(R.id.rating1);
        rating1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating = 1;
                findViewById(R.id.contentAddNote).setAlpha((float)1);
                findViewById(R.id.fragmentFabDoneAdd).setAlpha((float)1);
                findViewById(R.id.ratingsIncluded).setVisibility(View.GONE);
            }
        });
        rating2 = findViewById(R.id.rating2);
        rating2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating = 2;
                findViewById(R.id.contentAddNote).setAlpha((float)1);
                findViewById(R.id.fragmentFabDoneAdd).setAlpha((float)1);
                findViewById(R.id.ratingsIncluded).setVisibility(View.GONE);
            }
        });
        rating3 = findViewById(R.id.rating3);
        rating3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating = 3;
                findViewById(R.id.contentAddNote).setAlpha((float)1);
                findViewById(R.id.fragmentFabDoneAdd).setAlpha((float)1);
                findViewById(R.id.ratingsIncluded).setVisibility(View.GONE);
            }
        });
        rating4 = findViewById(R.id.rating4);
        rating4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating = 4;
                findViewById(R.id.contentAddNote).setAlpha((float)1);
                findViewById(R.id.fragmentFabDoneAdd).setAlpha((float)1);
                findViewById(R.id.ratingsIncluded).setVisibility(View.GONE);
            }
        });
        rating5 = findViewById(R.id.rating5);
        rating5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rating = 5;
                findViewById(R.id.contentAddNote).setAlpha((float)1);
                findViewById(R.id.fragmentFabDoneAdd).setAlpha((float)1);
                findViewById(R.id.ratingsIncluded).setVisibility(View.GONE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST ) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                placeName = place.getName().toString();
                newCoords = new Coordinates
                        (place.getLatLng().latitude, place.getLatLng().longitude);

            }
        }

        if(requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK){
            imageAsBitmap = (Bitmap) data.getExtras().get("data");
        }
    }

    private void uploadNote(){

        final String userID = getIntent().getStringExtra("userID");

        BaseEntry addEntry = new BaseEntry(newName, noteNotes, newCoords, placeName,
                newDate, 1, rating, UUID.randomUUID().toString(), imageURL, userID);

        databaseReference.child("posts").child(userID)
                .child(addEntry.getDatabaseID()).setValue(addEntry);
        databaseReference.child("locations").child(userID).child(addEntry.getDatabaseID())
                .setValue(addEntry.getCoordinates());
        databaseReference.child("dates").child(userID)
                .setValue(addEntry.getNoteDate());
    }

    private void uploadImage() throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageAsBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInBytes = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8")+".jpg";
        StorageReference newImageRef = storageRef.child(newImage);
        StorageReference newImageImagesRef = storageRef.child("images/"+newImage);
        newImageRef.getName().equals(newImageImagesRef.getName());    // true
        newImageRef.getPath().equals(newImageImagesRef.getPath());    // false

        UploadTask uploadTask = newImageImagesRef.putBytes(imageInBytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("imagestuff", "not uploaded");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                imageURL = taskSnapshot.getDownloadUrl().toString();
                uploadNote();
                //Log.d("imagestuff", "download url is " + imageURL);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {
                // Toast...
            }

            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA);
        } else {
            // we have the permission
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, REQUEST_CODE_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission granted, jupeee!",
                        Toast.LENGTH_SHORT).show();

                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, REQUEST_CODE_CAMERA);
            } else {
                Toast.makeText(this, "Permission not granted :(",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}

