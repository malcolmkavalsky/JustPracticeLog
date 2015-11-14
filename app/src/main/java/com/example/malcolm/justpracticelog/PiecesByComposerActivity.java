package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PiecesByComposerActivity extends Activity {

    ListView listView;
    EditText editTextPiece;
    EditText editTextComposer;
    ArrayList<String> displayList;
    ArrayAdapter<String> adapter;
    Composer composer;
    List<Piece> lpieces;
    Shortcuts shortcuts;

    private OkCancelDialog deleteComposerDialog;
    private OkCancelDialogListener deleteComposerDialogListener =
            new OkCancelDialogListener() {
                public void ok() {
                    deleteComposer();
                    setResult(Activity.RESULT_CANCELED, null);
                    finish();
                }

                public void cancel() {
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pieces_by_composer);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new choosePiece());

        editTextPiece = (EditText) findViewById(R.id.editTextPiece);
        editTextComposer = (EditText) findViewById(R.id.editTextComposer);
        Bundle bundle = getIntent().getExtras();
        long composerId = bundle.getLong("composerid");
        composer = new Composer(composerId);
        editTextComposer.setText(composer.getName());

        deleteComposerDialog = new OkCancelDialog(this);
        deleteComposerDialog.setListener(deleteComposerDialogListener);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        doRefresh();

        shortcuts = new Shortcuts(this, 1);
    }

    public void deleteComposer() {
        // Delete all pieces by composer
        for (Piece piece : Piece.getPiecesByComposerId(composer.getMyid())) {
            Piece.delete(piece);
        }
        Composer.delete(composer);
    }

    public void doRefresh() {
        lpieces = Piece.getPiecesByComposerId(composer.getMyid());
        displayList = generateDisplayList();
        Collections.sort(displayList);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public ArrayList<String> generateDisplayList() {
        ArrayList<String> result = new ArrayList<String>();
        for (Piece piece : lpieces) {
            result.add(piece.getName());
        }
        return result;
    }

    public boolean validName(String name) {
        if (name == null) return false;
        if (name.length() < 3) return false;

        return true;
    }


    public void backButtonHandler(View v) {
        setResult(Activity.RESULT_CANCELED, null);
        finish();

    }

    public void addButtonHandler(View v) {
        String name = editTextPiece.getText().toString().trim();

        if (!validName(name))
            Toast.makeText(getApplicationContext(), "Invalid name", Toast.LENGTH_SHORT).show();
        else if (Piece.getPieceByNameAndComposerId(name, composer.getMyid()) != null)
            Toast.makeText(getApplicationContext(), "Already exists", Toast.LENGTH_SHORT).show();
        else {
            Piece.add(name, composer.getMyid());
            editTextPiece.setText("");
            doRefresh();
        }
    }

    public void deleteButtonHandler(View v) {
        int npieces = lpieces.size();
        if (npieces > 0)
            deleteComposerDialog.open("Delete also " + npieces + " pieces by " + composer.getName());
        else {
            deleteComposer();
            setResult(Activity.RESULT_CANCELED, null);
            finish();
        }
    }

    public void renameButtonHandler(View v) {
        String name = editTextComposer.getText().toString().trim();

        if (!validName(name))
            Toast.makeText(getApplicationContext(), "Invalid name", Toast.LENGTH_SHORT).show();
        else if (Composer.getComposerByName(name) != null)
            Toast.makeText(getApplicationContext(), "Already exists", Toast.LENGTH_SHORT).show();
        else {
            composer.setName(name);
            Composer.update(composer);
            setResult(Activity.RESULT_CANCELED, null);
            finish();
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private class choosePiece implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            String item = displayList.get(position);
            Piece piece = Piece.getPieceByNameAndComposerId(item, composer.getMyid());

            Bundle bundle = new Bundle();
            bundle.putLong("pieceid", piece.getMyid());
            Intent intent = new Intent(PiecesByComposerActivity.this, PieceViewActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK)
            doRefresh();
    }
}
