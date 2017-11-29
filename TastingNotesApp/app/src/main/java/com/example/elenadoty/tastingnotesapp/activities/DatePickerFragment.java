package com.example.elenadoty.tastingnotesapp.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;

import com.example.elenadoty.tastingnotesapp.data.NoteDate;
import com.google.android.gms.common.api.Api;

import static com.google.android.gms.measurement.internal.zzl.api;

/**
 * Created by elenadoty on 11/25/17.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int year = 2000;
        int month = 1;
        int day = 1;

        if(Build.VERSION.SDK_INT >= 24) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        AddNoteActivity.newDate = new NoteDate(month, day, year);
    }
}