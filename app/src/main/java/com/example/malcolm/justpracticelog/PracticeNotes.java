package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.content.DialogInterface.*;

public class PracticeNotes  {
    Activity parent;
    ImageButton addNoteButton;
    EditText addNote;
    Dialog dlg;
    ListView myListView;
    List<Note> myListItems;
    int currentItemIndex;
    long pieceId;

    PracticeNotes(Activity parent, long pieceId) {
        this.parent = parent;
        this.pieceId = pieceId;
        addNoteButton = (ImageButton) parent.findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(new plusHandler());
        addNote = (EditText) parent.findViewById(R.id.editNote);
        myListView = (ListView) parent.findViewById(R.id.notesListView);
        myListView.setOnItemClickListener(onListClick);
        refresh();
        if (pieceId == 0) { // Invalid state, hide all GUI
            addNoteButton.setVisibility(View.INVISIBLE);
            myListView.setVisibility(View.INVISIBLE);
            addNote.setVisibility(View.INVISIBLE);
        }
        addNote.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    addCurrentEntry();
                    return true;
                }
                return false;
            }
        });
    }

    public void close() {
        myListItems = null;
    }

    public void loadItems() {
        myListItems = Note.getAllNotesForPiece(pieceId);
        // Cleanup database of empty items, possibly from previous bug
        boolean itemListAltered = false;
        for (Note item : myListItems) {
            if (item.getText().length() == 0) {
                Note.delete(item);
                itemListAltered = true;
            }
        }
        if (itemListAltered)
            myListItems = Note.getAllNotesForPiece(pieceId);// Re-get the list

        Log.d("PracticeNote", "Loaded " + myListItems.size() + " notes");
    }

    public void deleteCurrentItem() {
        Note currentItem = myListItems.get(currentItemIndex);
        Note.delete(currentItem);
        refresh();
    }

    public void saveCurrentItem(String msg) {
        if (msg.trim().length() == 0)
            deleteCurrentItem();
        else {
            Note currentItem = myListItems.get(currentItemIndex);
            currentItem.setText(msg);
            Note.update(currentItem);
            refresh();
        }
    }

    public void insertNewItem(String msg) {
        if (msg.trim().length() == 0)
            return;
        Note note = new Note();
        note.setPieceId(pieceId);
        note.setText(msg);
        note.setYmd(MyDate.today());
        note.setHms(MyTime.now());
        Note.create(note);
        refresh();
    }

    private void refresh() {
        loadItems();
        updateList();
        myListView.invalidateViews();
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentItemIndex = position;
            Note currentItem = myListItems.get(position);
            dlg = openAlertDialog(currentItem.getText());
            dlg.show();
        }
    };

    private Dialog openAlertDialog(String note) {
        LayoutInflater factory = LayoutInflater.from(parent);

        final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);

        final EditText noteText = (EditText) textEntryView.findViewById(R.id.alertEditText);
        noteText.setText(note);

        return new AlertDialog.Builder(parent)
                .setView(textEntryView)
                .setPositiveButton("Save", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        saveCurrentItem(noteText.getText().toString());
                    }
                })
                .setNeutralButton("Delete", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteCurrentItem();
                    }
                })
                .setNegativeButton("Cancel", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
    }

    private class plusHandler implements View.OnClickListener {
        public void onClick(View v) {
            addCurrentEntry();
        }
    }

    public void addCurrentEntry() {
        String str = addNote.getText().toString().trim();
        if (str.length() > 0) {
            insertNewItem(str);
            addNote.setText("");
        }
        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) parent.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(addNote.getWindowToken(), 0);
    }

    private void updateList() {
        final LayoutInflater inflater = parent.getLayoutInflater();

        myListView.setAdapter(new ArrayAdapter<Note>(parent, R.layout.list_row_note_details, myListItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row;
                if (null == convertView) {
                    row = inflater.inflate(R.layout.list_row_note_details, null);
                } else {
                    row = convertView;
                }
                Note item = getItem(position);
                TextView tv = (TextView) row.findViewById(R.id.rowTitle);
                String title = MyDate.toString(item.getYmd()) + " " + MyTime.toHourMins(item.getHms());
                tv.setText(title);
                TextView tv2 = (TextView) row.findViewById(R.id.rowDetails);
                String details = item.getText();
                if (details == null)
                    tv2.setText("");
                else {
                    tv2.setText(item.getText());
                }
                return row;
            }
        });
        myListView.invalidateViews();
    }
}
