package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.widget.NumberPicker;
import android.widget.TimePicker;import java.lang.Override;import java.lang.String;
import java.util.List;

/**
 * Created by malcolm on 11/23/15.
 */
public class PriorityChooserDialog {
    private Activity parent;
    private ListChooserDialog dialog;
    private ListChooserListener listChooserListener =
            new ListChooserListener() {
                public void chose(int index) {
                    Integer priority = priorityList.get(index);
                    priorityChooserListener.chose(priority.intValue());
                }

                public void cancelled() {
                    priorityChooserListener.cancelled();
                }

                public void add() {
                }

                public void delete() {
                }
            };

    List<Integer> priorityList;
    PriorityChooserListener priorityChooserListener;

    PriorityChooserDialog(Activity parent) {
        this.parent = parent;
        dialog = new ListChooserDialog(parent);
        dialog.setListChooserListener(listChooserListener);
    }

    public void enableAdd() {
        dialog.enableAdd();
    }
    public void enableDelete() {
        dialog.enableDelete();
    }
    public void open() {

        priorityList = Plan.getAllPriorities();

        CharSequence[] names = new CharSequence[priorityList.size()];
        int i=0;
        for(Integer item : priorityList ) {
            names[i] = item.toString();
            i++;
        }

        dialog.open("Select Priority", names);
    }
    public void setListener(PriorityChooserListener priorityChooserListener) {
        this.priorityChooserListener = priorityChooserListener;
    }
}
