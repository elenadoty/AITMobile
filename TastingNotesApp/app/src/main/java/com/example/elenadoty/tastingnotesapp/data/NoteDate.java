package com.example.elenadoty.tastingnotesapp.data;

/**
 * Created by elenadoty on 11/25/17.
 */

public class NoteDate {
    private int month;
    private int day;
    private int year;

    public NoteDate() {
    }

    public NoteDate(int month, int day, int year) {
        this.month = month;
        this.day = day;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return 1+month+"/"+day+"/"+year;
    }
}
