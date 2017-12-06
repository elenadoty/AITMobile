package com.example.elenadoty.tastingnotesapp.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by elenadoty on 11/22/17.
 */

public class BaseEntry {

    private String noteName;
    private String noteNotes;
    private NoteDate noteDate;
    private int noteType;
    private Coordinates coordinates;
    private String userID;
    private String placeName;
    private String databaseID;
    private String imageURL;
    private String userName;
    private int noteRating;

    public BaseEntry(String noteName, String noteNotes, Coordinates coordinates, String placeName,
                     NoteDate noteDate, int noteType, int noteRating,
                     String databaseID, String imageURL, String userID, String userName) {
        this.noteName = noteName;
        this.noteNotes = noteNotes;
        this.placeName = placeName;
        this.noteDate = noteDate;
        this.coordinates = coordinates;
        this.noteType = noteType;
        this.databaseID = databaseID;
        this.noteRating = noteRating;
        this.imageURL = imageURL;
        this.userID = userID;
        this.userName = userName;
    }

    public BaseEntry() {}

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNoteNotes() {
        return noteNotes;
    }

    public void setNoteNotes(String noteNotes) {
        this.noteNotes = noteNotes;
    }

    public NoteDate getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(NoteDate noteDate) {
        this.noteDate = noteDate;
    }

    public int getNoteType() {
        return noteType;
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    /*public LatLng getLatLng(){
        return new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
    }*/

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }


    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public int getNoteRating() {
        return noteRating;
    }

    public void setNoteRating(int noteRating) {
        this.noteRating = noteRating;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
