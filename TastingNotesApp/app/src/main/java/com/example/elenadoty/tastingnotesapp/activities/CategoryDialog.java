package com.example.elenadoty.tastingnotesapp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.elenadoty.tastingnotesapp.R;

/**
 * Created by ciaragoetze on 12/9/17.
 */

public class CategoryDialog extends DialogFragment {
    private Spinner spCategory;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.get_category_dialog, null))
                .setPositiveButton("Done", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CategoryDialog.this.getDialog().cancel();
                    }
                });

        final AlertDialog alertDialog = builder.create();



        // Create the AlertDialog object and return it
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                spCategory = alertDialog.findViewById(R.id.spCategory);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.category_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategory.setAdapter(adapter);

                Button button = ((AlertDialog) dialog)
                        .getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        final String category = spCategory.getSelectedItem().toString();

                        ArrayAdapter<CharSequence> spinnerAdapter =
                                ArrayAdapter.createFromResource(getContext(),
                                R.array.category_array, android.R.layout.simple_spinner_item);

                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spCategory.setAdapter(spinnerAdapter);

                        if (spCategory.getSelectedItem() != null) {
                            AddNoteActivity.addCategory(category);
                            dialog.dismiss();

                        }
                    }
                });
            }
        });
        return alertDialog;
    }

}
