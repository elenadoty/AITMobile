package com.example.elenadoty.tastingnotesapp.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by elenadoty on 11/22/17.
 */

public class BaseEntry {

    private String noteName = "unnamed";
    private String noteNotes = "[no notes recorded]";
    private String noteCategory = "other";
    private NoteDate noteDate = new NoteDate(12, 25, 2017);
    private Coordinates coordinates = new Coordinates(43.45410580000001, -80.49917839999999);
    private String userID;
    private String placeName = "Google";
    private String databaseID;
    private String imageURL;
    private String userName;
    private int noteRating = 5;

    public BaseEntry(String noteName, String noteNotes, Coordinates coordinates, String placeName,
                     NoteDate noteDate, int noteRating,
                     String databaseID, String imageURL, String userID, String userName, String noteCategory) {
        this.noteName = noteName;
        this.noteNotes = noteNotes;
        this.placeName = placeName;
        this.noteDate = noteDate;
        this.coordinates = coordinates;
        this.databaseID = databaseID;
        this.noteRating = noteRating;
        this.imageURL = imageURL;
        this.userID = userID;
        this.userName = userName;
        this.noteCategory = noteCategory;
    }

    public BaseEntry() {}

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNoteCategory() {
        return noteCategory;
    }

    public void setNoteCategory(String noteCategory) {
        this.noteCategory = noteCategory;
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

    public String getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

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
