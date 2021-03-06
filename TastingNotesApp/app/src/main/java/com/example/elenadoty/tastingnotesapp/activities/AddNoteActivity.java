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
    private static BaseEntry newEntry;
    static Bitmap imageAsBitmap;

    ImageButton rating1, rating2, rating3, rating4, rating5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        newEntry = new BaseEntry();

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
                try {
                    uploadImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        dealWithVoronoi();

        databaseReference = FirebaseDatabase.getInstance().getReference();


    }

    private void dealWithVoronoi() {
        //TODO: Clean this up?
        VoronoiView voronoiView = (VoronoiView) findViewById(R.id.voronoi_view);
        LayoutInflater layoutInflater = getLayoutInflater();

        for (int i = 0; i < 7; i++) {
            RelativeLayout rl = (RelativeLayout) layoutInflater.inflate(R.layout.item_voronoi, null, false);
            final KenBurnsView viewToAdd = (KenBurnsView) rl.findViewById(R.id.image_voronoi);

            final int j = i;
            viewToAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (j) {
                        case 0:
                            //name
                            android.support.v4.app.DialogFragment newNameFragment = new NameDialog();
                            newNameFragment.show(getSupportFragmentManager(), "name_fragment");
                            viewToAdd.setImageResource(R.drawable.prop_done_icon);
                            viewToAdd.setEnabled(false);

                            break;

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

                        case 5:
                            //camera stuff
                            requestNeededPermission();
                            viewToAdd.setImageResource(R.drawable.prop_done_icon);
                            viewToAdd.setEnabled(false);

                            break;

                        case 6:
                            //category
                            android.support.v4.app.DialogFragment newCategoryFragment = new CategoryDialog();
                            newCategoryFragment.show(getSupportFragmentManager(), "category_fragment");
                            viewToAdd.setImageResource(R.drawable.prop_done_icon);
                            viewToAdd.setEnabled(false);

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
                case 6:
                    viewToAdd.setImageResource(R.drawable.cancel_icon);
                case 0:
                    viewToAdd.setImageResource(R.drawable.descriptions);
                    break;
                default:
                    break;
            }
            voronoiView.addView(rl);
        }
    }

    public static void addName(String newName){
        newEntry.setNoteName(newName);
    }

    public static void addNotes(String notes){
        newEntry.setNoteNotes(notes);
    }

    public static void addDate(NoteDate date){
        newEntry.setNoteDate(date);
    }

    public static void addCategory(String category){
        newEntry.setNoteCategory(category);
    }

    private void setRatingOnClick() {
        rating1 = findViewById(R.id.rating1);
        rating1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEntry.setNoteRating(1);
                findViewById(R.id.contentAddNote).setAlpha((float)1);
                findViewById(R.id.fragmentFabDoneAdd).setAlpha((float)1);
                findViewById(R.id.ratingsIncluded).setVisibility(View.GONE);
            }
        });
        rating2 = findViewById(R.id.rating2);
        rating2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEntry.setNoteRating(2);
                findViewById(R.id.contentAddNote).setAlpha((float)1);
                findViewById(R.id.fragmentFabDoneAdd).setAlpha((float)1);
                findViewById(R.id.ratingsIncluded).setVisibility(View.GONE);
            }
        });
        rating3 = findViewById(R.id.rating3);
        rating3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEntry.setNoteRating(3);
                findViewById(R.id.contentAddNote).setAlpha((float)1);
                findViewById(R.id.fragmentFabDoneAdd).setAlpha((float)1);
                findViewById(R.id.ratingsIncluded).setVisibility(View.GONE);
            }
        });
        rating4 = findViewById(R.id.rating4);
        rating4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEntry.setNoteRating(4);
                findViewById(R.id.contentAddNote).setAlpha((float)1);
                findViewById(R.id.fragmentFabDoneAdd).setAlpha((float)1);
                findViewById(R.id.ratingsIncluded).setVisibility(View.GONE);
            }
        });
        rating5 = findViewById(R.id.rating5);
        rating5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEntry.setNoteRating(5);
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
                newEntry.setPlaceName(place.getName().toString());
                newEntry.setCoordinates(new Coordinates(place.getLatLng().latitude,
                        place.getLatLng().longitude));

            }
        }

        if(requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK){
            imageAsBitmap = (Bitmap) data.getExtras().get("data");
        }
    }

    private void uploadNote(){

        final String userID = getIntent().getStringExtra("userID");

        newEntry.setDatabaseID(UUID.randomUUID().toString());
        newEntry.setUserID(userID);
        newEntry.setUserName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());


        databaseReference.child("posts").child(userID)
                .child(newEntry.getDatabaseID()).setValue(newEntry);

    }

    private void uploadImage() throws Exception{
        if(imageAsBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageAsBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageInBytes = baos.toByteArray();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            String newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8") + ".jpg";
            StorageReference newImageRef = storageRef.child(newImage);
            StorageReference newImageImagesRef = storageRef.child("images/" + newImage);
            newImageRef.getName().equals(newImageImagesRef.getName());
            newImageRef.getPath().equals(newImageImagesRef.getPath());

            UploadTask uploadTask = newImageImagesRef.putBytes(imageInBytes);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    newEntry.setImageURL("https://firebasestorage.googleapis.com/v0/b/tastingnotes-b10cd.appspot.com/o/images%2Fgourmet_food.png?alt=media&token=0dc128d3-56e9-44f4-ba57-2f8c332a093e");
                    uploadNote();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    newEntry.setImageURL(taskSnapshot.getDownloadUrl().toString());
                    uploadNote();
                }
            });
        }
        else {
            newEntry.setImageURL("https://firebasestorage.googleapis.com/v0/b/tastingnotes-b10cd.appspot.com/o/images%2Fgourmet_food.png?alt=media&token=0dc128d3-56e9-44f4-ba57-2f8c332a093e");
            uploadNote();
        }
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
            }

            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA);
        } else {
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

                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, REQUEST_CODE_CAMERA);
            } else {
            }
        }
    }
}

