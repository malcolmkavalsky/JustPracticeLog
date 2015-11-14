package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

/**
 * Created by malcolm on 6/27/15.
 */

public class OkCancelDialog {
    private Dialog dialog;
    private Activity parent;
    private OkCancelDialogListener listener;

    OkCancelDialog(Activity parent) {
        this.parent = parent;
    }

    public void open(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setTitle(title);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                listener.cancel();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                listener.ok();
            }
        });
        dialog = builder.create();
        dialog.show();
    }
    public void setListener(OkCancelDialogListener l) {
        listener = l;
    }
}
