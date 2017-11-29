package com.example.elenadoty.tastingnotesapp.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.elenadoty.tastingnotesapp.R;

/**
 * Created by elenadoty on 11/25/17.
 */

public class NameDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.get_name_dialog, null))
                .setPositiveButton("Done", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NameDialog.this.getDialog().cancel();
                    }
                });

        final AlertDialog dialog = builder.create();

        // Create the AlertDialog object and return it
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog)
                        .getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditText editText = getDialog().findViewById(R.id.dialog_enterName);
                        if (!TextUtils.isEmpty(editText.getText())) {
                            String newName = editText.getText().toString();
                            AddNoteActivity.newName = newName;
                            dialog.dismiss();

                        } else {
                            editText.setError("Must enter a name.");
                        }
                    }
                });
            }
        });
        return dialog;
    }
}