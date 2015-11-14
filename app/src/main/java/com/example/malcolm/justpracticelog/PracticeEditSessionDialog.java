package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by malcolm on 7/5/15.
 */
public class PracticeEditSessionDialog {

    private Dialog dialog;
    private Activity parent;
    private PracticeEditSessionDialogListener listener;
    private EditText noteText;
    private EditText durationText;
    private TextView title;

    PracticeEditSessionDialog(Activity parent, PracticeEditSessionDialogListener l) {
        this.parent = parent;
        this.listener = l;
    }

    public void open(final int ymd, final long pieceId, int minutes, String notes) {
        LayoutInflater factory = LayoutInflater.from(parent.getApplicationContext());
        final View textEntryView = factory.inflate(R.layout.practice_session_dialog, null);

        Piece piece = new Piece(pieceId);
        title = (TextView) textEntryView.findViewById(R.id.title);
        title.setText(piece.getName());

        durationText = (EditText) textEntryView.findViewById(R.id.durationText);
        durationText.setText(Integer.toString(minutes));

        noteText = (EditText) textEntryView.findViewById(R.id.notesEditText);
        noteText.setText(notes);

        final ImageButton plusButton = (ImageButton) textEntryView.findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new plusButtonHandler());
        final ImageButton minusButton = (ImageButton) textEntryView.findViewById(R.id.minusButton);
        minusButton.setOnClickListener(new minusButtonHandler());


        AlertDialog.Builder builder = new AlertDialog.Builder(parent);

        builder.setView(textEntryView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        listener.save(ymd, pieceId, Integer.parseInt(durationText.getText().toString()), noteText.getText().toString());
                    }
                });

        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                listener.delete(ymd, pieceId);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private class plusButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            int mins = Integer.parseInt(durationText.getText().toString());
            mins++;
            durationText.setText(Integer.toString(mins));
        }
    }
    private class minusButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            int mins = Integer.parseInt(durationText.getText().toString());
            mins--;
            if( mins < 0) mins = 0;
            durationText.setText(Integer.toString(mins));
        }
    }
}
