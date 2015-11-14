package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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


public class PiecesActivity extends Activity {
    ListView listView;
    EditText editText;
    List<ListDualRowItem> displayList;
    Shortcuts shortcuts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pieces);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new chooseComposer());
        editText = (EditText) findViewById(R.id.editText);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        doRefresh();
        shortcuts = new Shortcuts(this,1);
    }
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        doRefresh();
    }

    public void doRefresh() {
        displayList = generateDisplayList();
        Collections.sort(displayList, new Comparator<ListDualRowItem>() {
            public int compare(ListDualRowItem s1, ListDualRowItem s2) {
                return s1.getTop().compareToIgnoreCase(s2.getTop());
            }
        });
        updateList(displayList);
    }

    public List<ListDualRowItem> generateDisplayList() {
        List<Composer> lcomposers = Composer.getAllComposers();

        ArrayList<ListDualRowItem> result = new ArrayList<ListDualRowItem>();
        for (Composer composer : lcomposers) {
            ListDualRowItem item = new ListDualRowItem();
            item.setReference(composer.getMyid());
            item.setTop(composer.getName()) ;
            String bottom = "";
            List<Piece> lpieces = Piece.getPiecesByComposerId(composer.getMyid());

            Collections.sort(lpieces, new Comparator<Piece>() {
                public int compare(Piece s1, Piece s2) {
                    return s1.getName().compareToIgnoreCase(s2.getName());
                }
            });

            int cnt = 0;
            for (Piece piece : lpieces) {
                bottom += piece.getName();
                cnt++;
                if (cnt < lpieces.size())
                    bottom += ",";
            }
            item.setBottom(bottom);
            result.add(item);
        }
        return result;
    }

    private void updateList(List<ListDualRowItem> aList) {
        final LayoutInflater inflater = this.getLayoutInflater();

        listView.setAdapter(new ArrayAdapter<ListDualRowItem>(this, R.layout.list_row_title_details, aList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row;
                if (null == convertView) {
                    row = inflater.inflate(R.layout.list_row_title_details, null);
                } else {
                    row = convertView;
                }
                ListDualRowItem item = getItem(position);

                TextView tv = (TextView) row.findViewById(R.id.rowTitle);
                tv.setText(item.getTop());
                TextView tv2 = (TextView) row.findViewById(R.id.rowDetails);
                String details = item.getBottom();
                if (details == null)
                    tv2.setText("");
                else
                    tv2.setText(item.getBottom());

                return row;
            }
        });
        listView.invalidateViews();
    }
    public boolean validName(String name) {
        if (name == null) return false;
        name = name.trim();
        if (name.length() < 2)
            return false;

        return true;
    }

    public void addButtonHandler(View v) {
        String name = editText.getText().toString();
        if( !validName(name))
            Toast.makeText(getApplicationContext(), "Invalid name", Toast.LENGTH_SHORT).show();
        else if (Composer.getComposerByName(name) != null)
            Toast.makeText(getApplicationContext(), "Already exists", Toast.LENGTH_SHORT).show();
        else {
            Composer composer = new Composer();
            composer.setName(name);
            Composer.create(composer);
            editText.setText("");
            doRefresh();
        }
    }

    public void backButtonHandler(View v) {
        setResult(Activity.RESULT_CANCELED, null);
        finish();

    }

    public void onDestroy() {
        super.onDestroy();
    }

    private class chooseComposer implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            ListDualRowItem item = displayList.get(position);
            long composerId = item.getReference();
            Bundle bundle = new Bundle();
            bundle.putLong("composerid", composerId);
            Intent intent = new Intent(PiecesActivity.this, PiecesByComposerActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
        }

    }
}
