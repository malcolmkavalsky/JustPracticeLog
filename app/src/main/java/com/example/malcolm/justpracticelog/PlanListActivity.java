package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class PlanListActivity extends Activity {
    List<ListQuadRowItem> displayList;
    ListView listView;
    List<TextView> dayText = new ArrayList<TextView>();
    Shortcuts shortcuts;

    // --- Piece Chooser ---
    PieceChooserDialog pieceChooserDialog;
    private PieceChooserListener mListener =
            new PieceChooserListener() {
                public void chose(long pieceid) {
                    // Don't allow two plans for the same piece !!!
                    if ( Plan.getForPieceId(pieceid) != null)
                        Toast.makeText(getApplicationContext(), "Already exists", Toast.LENGTH_SHORT).show();
                    else {
                        Plan plan = new Plan(pieceid, "", "", 15, 100, "SMTWTF_");
                        Plan.add(plan);
                        doRefresh();
                    }
                }
                public void cancelled() {
                }

                public void all() {
                }

                public void add() {
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new choosePlan());
        dayText.add((TextView) findViewById(R.id.day0));
        dayText.add((TextView) findViewById(R.id.day1));
        dayText.add((TextView) findViewById(R.id.day2));
        dayText.add((TextView) findViewById(R.id.day3));
        dayText.add((TextView) findViewById(R.id.day4));
        dayText.add((TextView) findViewById(R.id.day5));
        dayText.add((TextView) findViewById(R.id.day6));
        doRefresh();
        shortcuts = new Shortcuts(this,2);
        pieceChooserDialog = new PieceChooserDialog(this);
        pieceChooserDialog.setListener(mListener);
    }

    public void onResume() {  // After a pause OR at startup
        super.onResume();
        doRefresh();
    }

    public void backButtonHandler(View v) {
        setResult(Activity.RESULT_CANCELED, null);
        finish();

    }

    public void addButtonHandler(View v) {
        pieceChooserDialog.open();
//        Intent intent = new Intent(PlanListActivity.this, PiecesActivity.class);
//        startActivityForResult(intent, 0);
    }

    public void doRefresh() {
        displayList = generateDisplayList();
        updateList(displayList);
    }

    public List<ListQuadRowItem> generateDisplayList() {
        List<Plan> lplans = Plan.getAllPlans();
        Collections.sort(lplans, new Comparator<Plan>() {
            public int compare(Plan s1, Plan s2) {
                return (s1.getPriority() - s2.getPriority());

            }
        });
        updateTotals(lplans);

        ArrayList<ListQuadRowItem> result = new ArrayList<ListQuadRowItem>();
        for (Plan plan : lplans) {
            Piece piece = new Piece(plan.getPieceId());
            Composer composer = new Composer(piece.getComposerId());

            ListQuadRowItem item = new ListQuadRowItem();
            item.setReference(plan.getMyid());

            item.setTopLeft(plan.getPriority() + "." + piece.getName());
            item.setBottomLeft(composer.getName());
            item.setTopRight(plan.getDays());
            item.setBottomRight(MyTime.minutesToString(plan.getMinutes()));

            result.add(item);
        }
        ListQuadRowItem item = new ListQuadRowItem();
        item.setReference(-1);
        result.add(item);

        return result;
    }

    private void updateTotals(List<Plan> lplans) {
        Integer[] dayTotal = {0, 0, 0, 0, 0, 0, 0};
        for (Plan item : lplans) {
            String daystr = item.getDays();
            for (int day = 0; day < 7; day++)
                if (daystr.charAt(day) != '_')
                    dayTotal[day] += item.getMinutes();
        }
        for (int day = 0; day < 7; day++)
            dayText.get(day).setText(MyTime.minutesToString(dayTotal[day]));
    }

    private void updateList(List<ListQuadRowItem> aList) {
        final LayoutInflater inflater = this.getLayoutInflater();

        listView.setAdapter(new ArrayAdapter<ListQuadRowItem>(this, R.layout.list_row_table, aList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row;
                if (null == convertView) {
                    row = inflater.inflate(R.layout.list_row_table, null);
                } else {
                    row = convertView;
                }
                ListQuadRowItem item = getItem(position);

                ((TextView) row.findViewById(R.id.rowTopRight)).setText(item.getTopRight());
                ((TextView) row.findViewById(R.id.rowBottomLeft)).setText(item.getBottomLeft());
                ((TextView) row.findViewById(R.id.rowBottomRight)).setText(item.getBottomRight());

                TextView tv = (TextView) row.findViewById(R.id.rowTopLeft);
                if( item.getReference() == -1) {
                    tv.setText("Add piece ...");
                    tv.setTextColor(Color.BLUE);
                } else {
                    tv.setText(item.getTopLeft());
                    tv.setTextColor(Color.GRAY);
                }

                return row;
            }
        });
        listView.invalidateViews();
    }

    private class choosePlan implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            ListQuadRowItem item = displayList.get(position);
            long planId = item.getReference();
            if( planId == -1 )
                pieceChooserDialog.open();
            else {
                Bundle bundle = new Bundle();
                bundle.putLong("planid", planId);
                Intent intent = new Intent(PlanListActivity.this, PlanEditorActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK)
            doRefresh();
    }
}
