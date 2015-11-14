package com.example.malcolm.justpracticelog;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PracticeListActivity extends Activity {
    List<ListQuadRowItem> displayList;
    ListView listView;
    List<TextView> dayText = new ArrayList<TextView>();
    Shortcuts shortcuts;
    Progress myProgress;
    PracticeEditSessionDialog editSessionDialog;
    PracticeEditSessionDialogListener myListener = new PracticeEditSessionDialogListener() {
        @Override
        public void cancelled() {

        }

        @Override
        public void save(int ymd, long pieceId, int minutes, String notes) {
            Note.replaceNotes(ymd, pieceId, notes);
            Practice.replaceDuration(ymd, pieceId, minutes);
            doRefresh();
        }

        @Override
        public void add(int ymd, long pieceId, int minutes, String notes) {

        }

        @Override
        public void delete(int ymd, long pieceId) {
            Note.deleteNotes(ymd, pieceId);
            Practice.deleteAllPracticeSessions(ymd, pieceId);
            doRefresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_list);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new choosePractice());
        listView.setOnItemLongClickListener(new editPractice());

        dayText.add((TextView) findViewById(R.id.day0));
        dayText.add((TextView) findViewById(R.id.day1));
        dayText.add((TextView) findViewById(R.id.day2));
        dayText.add((TextView) findViewById(R.id.day3));
        dayText.add((TextView) findViewById(R.id.day4));
        dayText.add((TextView) findViewById(R.id.day5));
        dayText.add((TextView) findViewById(R.id.day6));
        shortcuts = new Shortcuts(this, 3);
        myProgress = new Progress(this);
        doRefresh();
    }

    public void onResume() {  // After a pause OR at startup
        super.onResume();
        doRefresh();
    }

    public void backButtonHandler(View v) {
        setResult(Activity.RESULT_CANCELED, null);
        finish();

    }

    public void doRefresh() {
        displayList = generateDisplayList();
        updateList(displayList);
        myProgress.update();
    }

    private ListQuadRowItem addRowHeader(String hdr) {
        ListQuadRowItem item = new ListQuadRowItem();
        item.setIsHeader(true);
        item.setTopLeft(hdr);
        item.setBottomRight("");
        item.setTopRight("");
        item.setBottomLeft("");
        return item;
    }

    private ListQuadRowItem addRow(long pieceId) {
        ListQuadRowItem item = new ListQuadRowItem();
        item.setIsHeader(false);

        Piece piece = new Piece(pieceId);
        Composer composer = new Composer(piece.getComposerId());
        int totalPracticed = Practice.totalWeeklyPracticeTimeForPiece(pieceId);
        int totalPlanned = Plan.totalWeeklyPlanTimeForPiece(pieceId);

        Plan plan = Plan.getForPieceId(pieceId);
        item.setReference(pieceId);
        if (plan != null) {
            item.setColor(Practice.getPracticePieceLabelColor(plan));
        } else {
            item.setColor(Color.BLACK);
        }
        item.setTopLeft(piece.getName());
        item.setBottomLeft(composer.getName());

        item.setTopRight(Practice.getDaysPracticed(pieceId));
        String practiced = MyTime.minutesToString(totalPracticed);
        String planned = MyTime.minutesToString(totalPlanned);
        item.setBottomRight(practiced + "/" + planned);

        return item;
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

        int today = MyDate.today();
        int today_no = MyDate.dayOfWeek(today) - 1;
        int startOfWeek = MyDate.startOfWeek(today);
        ArrayList<Plan> todaysPieces = new ArrayList<Plan>();
        ArrayList<Plan> otherPieces = new ArrayList<Plan>();
        for (Plan plan : lplans) {
            if (plan.getPracticeOn(today_no))
                todaysPieces.add(plan);
            else
                otherPieces.add(plan);
        }

        if (todaysPieces.size() > 0) {
            result.add(addRowHeader("Today's pieces"));
            for (Plan plan : todaysPieces) {
                result.add(addRow(plan.getPieceId()));
            }
        }
        if (otherPieces.size() > 0) {
            result.add(addRowHeader("Other day's pieces"));
            for (Plan plan : otherPieces) {
                result.add(addRow(plan.getPieceId()));
            }
        }
        // Finally add all practice items that weren't in the plan
        List<Long> practiced = Practice.getAllPiecesPracticedForWeek(startOfWeek);
        List<Long> others = new ArrayList<>();
        for (Long pieceId : practiced) {
            if (Plan.getForPieceId(pieceId) == null)
                others.add(pieceId);
        }
        if (others.size() > 0) {
            result.add(addRowHeader("Other pieces (not from plan)"));
            for (Long pieceid : others)
                result.add(addRow(pieceid));
        }
        return result;
    }

    private void updateTotals(List<Plan> lplans) {
        int ymd = MyDate.startOfWeek(MyDate.today());
        for (int day = 0; day < 7; day++) {
            int total = Practice.getTotalPracticeTime(ymd);
            dayText.get(day).setText(MyTime.minutesToString(total));
            ymd = MyDate.nextDay(ymd);
        }
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
                TextView tv_top_left = (TextView) row.findViewById(R.id.rowTopLeft);
                TextView tv_top_right = (TextView) row.findViewById(R.id.rowTopRight);
                TextView tv_bottom_left = (TextView) row.findViewById(R.id.rowBottomLeft);
                TextView tv_bottom_right = (TextView) row.findViewById(R.id.rowBottomRight);
                TableLayout tl = (TableLayout) row.findViewById(R.id.tableLayout);
                TableRow top_row = (TableRow) row.findViewById(R.id.topRow);
                TableRow bottom_row = (TableRow) row.findViewById(R.id.bottomRow);

                if (item.isHeader()) {
                    tl.setBackgroundColor(Color.BLUE);
                    // bottom_row_height = bottom_row.getHeight();
                    tv_top_left.setHeight(1);
                    tv_top_right.setHeight(1);
                    tv_bottom_left.setTextColor(Color.WHITE);
                    tv_bottom_left.setText(item.getTopLeft());
                    tv_bottom_right.setText("");
                } else {
                    tl.setBackgroundColor(Color.WHITE);
                    int dps = 22;
                    final float scale = getContext().getResources().getDisplayMetrics().density;
                    int pixels = (int) (dps * scale + 0.5f);
                    tv_top_left.setHeight(pixels);
                    tv_top_left.setText(item.getTopLeft());
                    tv_top_left.setTextColor(item.getColor());
                    tv_top_right.setText(item.getTopRight());
                    tv_bottom_left.setText(item.getBottomLeft());
                    tv_bottom_left.setTextColor(Color.GRAY);
                    tv_bottom_right.setText(item.getBottomRight());
                }
                return row;
            }
        });
        listView.invalidateViews();
    }

    private class choosePractice implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            ListQuadRowItem item = displayList.get(position);
            long pieceId = item.getReference();
            Bundle bundle = new Bundle();
            bundle.putLong("pieceid", pieceId);
            Intent intent = new Intent(PracticeListActivity.this, PracticeSessionActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 0);
        }

    }

    private class editPractice implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ListQuadRowItem item = displayList.get(position);
            long pieceId = item.getReference();
            openPracticeNotesEditDialog(MyDate.today(), pieceId);
            return false;
        }
    }

    private void openPracticeNotesEditDialog(int ymd, long pieceid) {
        String msg = Note.concatAllNotesForDay(ymd, pieceid);
        int minutes = Practice.totalDailyPracticeForPiece(pieceid, ymd);
        if (minutes == 0) {
            Plan plan = Plan.getForPieceId(pieceid);
            if (plan != null)
                minutes = plan.getMinutes();
            else
                minutes = 15; // Default 15 minutes
        }
        editSessionDialog = new PracticeEditSessionDialog(this, myListener);
        editSessionDialog.open(ymd, pieceid, minutes, msg);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK)
            doRefresh();
    }
}
