package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PracticeTabletActivity extends Activity {
    public final static int GREEN = 0xff00aa00;
    public final static int RED = 0xffdd0000;
    public final static int YELLOW = 0xffaaaa00;
    public final static int BLACK = 0xff000000;

    Shortcuts shortcuts;
    TableLayout myTable;
    List<TableRow> rows;
    Intent intent;
    String from;
    int startOfWeek;
    List<RowHolder> rowHolderList;
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
            Practice.deleteAllPracticeSessions(ymd, pieceId);
            Note.deleteNotes(ymd, pieceId);
            doRefresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_tablet);
        startOfWeek = MyDate.startOfWeek(MyDate.today());
        shortcuts = new Shortcuts(this, 3);
        myTable = (TableLayout) findViewById(R.id.TableLayout01);
        myProgress = new Progress(this);
        myProgress.update();
    }

    public void onResume() {
        super.onResume();
        doRefresh();
    }

    private void doRefresh() {
        TableLayout table = (TableLayout) findViewById(R.id.TableLayout01);
        table.removeAllViews();
        reloadModelView();
        myProgress.update();
    }

    private void reloadModelView() {
        List<Plan> lplans = Plan.getAllPlans();
        Collections.sort(lplans, new Comparator<Plan>() {
            public int compare(Plan s1, Plan s2) {
                return (s1.getPriority() - s2.getPriority());

            }
        });
        rowHolderList = new ArrayList<RowHolder>();
        addTableHeader();
        // First put all plan items for today

        int today = MyDate.today();
        int today_no = MyDate.dayOfWeek(today) - 1;

        ArrayList<Plan> todaysPieces = new ArrayList<Plan>();
        ArrayList<Plan> otherPieces = new ArrayList<Plan>();

        for (Plan plan : lplans) {
            if (plan.getPracticeOn(today_no))
                todaysPieces.add(plan);
            else
                otherPieces.add(plan);
        }

        if (todaysPieces.size() > 0) {
            addRowHeader("Today's pieces");
            for (Plan plan : todaysPieces)
                addRow(plan.getPieceId());
        }
        if (otherPieces.size() > 0) {
            addRowHeader("Other day's pieces");
            for (Plan plan : otherPieces)
                addRow(plan.getPieceId());
        }
        // Finally add all practice items that were't in the plan
        List<Long> practiced = Practice.getAllPiecesPracticedForWeek(startOfWeek);
        List<Long> others = new ArrayList<>();
        for(Long pieceId : practiced) {
            if( Plan.getForPieceId(pieceId) == null )
                others.add(pieceId);
        }
        if( others.size() > 0 ) {
            addRowHeader("Other pieces (not from plan)");
            for(Long pieceid : others)
                addRow(pieceid);
        }

    }

    private void addTableHeader() {
        TableRow tableRow = (TableRow) View.inflate(this, R.layout.activity_practice_tablet_header, null);
        myTable.addView(tableRow);
        // Fill in totals
        TextView[] dayTotalTextView = new TextView[7];
        dayTotalTextView[0] = (TextView) tableRow.findViewById(R.id.day0);
        dayTotalTextView[1] = (TextView) tableRow.findViewById(R.id.day1);
        dayTotalTextView[2] = (TextView) tableRow.findViewById(R.id.day2);
        dayTotalTextView[3] = (TextView) tableRow.findViewById(R.id.day3);
        dayTotalTextView[4] = (TextView) tableRow.findViewById(R.id.day4);
        dayTotalTextView[5] = (TextView) tableRow.findViewById(R.id.day5);
        dayTotalTextView[6] = (TextView) tableRow.findViewById(R.id.day6);

        int ymd = MyDate.startOfWeek(MyDate.today());
        for (int i = 0; i < 7; i++) {
            int mins = Practice.getTotalPracticeTime(ymd);
            dayTotalTextView[i].setText(MyTime.minutesToString(mins));
            ymd = MyDate.nextDay(ymd);
        }
    }

    private void addRowHeader(String title) {
        TableRow row = (TableRow) View.inflate(this, R.layout.activity_practice_tablet_row_header, null);
        // row.setBackgroundColor(0xff000000);
        TextView tv = (TextView) row.findViewById(R.id.title);
        tv.setText(title);
        myTable.addView(row);
    }

    private void addRow(long pieceId) {
        TableRow row = (TableRow) View.inflate(this, R.layout.activity_practice_tablet_row, null);
        myTable.addView(row);
        // row.setBackgroundColor(0xff000000);
        RowHolder rh = new RowHolder(row);
        rh.populate(pieceId, startOfWeek);
        rowHolderList.add(rh);
    }

    private class practiceButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            Piece piece = new Piece((long) v.getTag());
            ActivatePracticeSession(piece);
        }
    }


    private void ActivatePracticeSession(Piece piece) {

        Bundle bundle = new Bundle();
        bundle.putLong("pieceid", piece.getMyid());

        Intent intent = new Intent(this, PracticeSessionActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
    }

    private class RowHolder {
        private TableRow tableRow;
        private long pieceId;
        private TextView pieceNameTextView, goalTextView;
        private ImageButton practicePieceButton;
        private Button[] dayButton;
        int startOfWeek;


        RowHolder(View row) {
            tableRow = (TableRow) row;
            pieceNameTextView = (TextView) tableRow.findViewById(R.id.pieceNameTextView);
            practicePieceButton = (ImageButton) tableRow.findViewById(R.id.practicePieceButton);
            goalTextView = (TextView) tableRow.findViewById(R.id.goalTextView);

            dayButton = new Button[7];
            dayButton[0] = (Button) tableRow.findViewById(R.id.day0);
            dayButton[1] = (Button) tableRow.findViewById(R.id.day1);
            dayButton[2] = (Button) tableRow.findViewById(R.id.day2);
            dayButton[3] = (Button) tableRow.findViewById(R.id.day3);
            dayButton[4] = (Button) tableRow.findViewById(R.id.day4);
            dayButton[5] = (Button) tableRow.findViewById(R.id.day5);
            dayButton[6] = (Button) tableRow.findViewById(R.id.day6);
        }

        boolean populate(long pieceId, int startOfWeek) {
            this.pieceId = pieceId;
            this.startOfWeek = startOfWeek;


            // Start practice session by clicking on piece name or on + button
            Piece piece = new Piece(pieceId);
            pieceNameTextView.setText(piece.getName());
            pieceNameTextView.setTag(this.pieceId);
            pieceNameTextView.setOnClickListener(new practiceButtonHandler());
            practicePieceButton.setTag(this.pieceId);
            practicePieceButton.setOnClickListener(new practiceButtonHandler());


            Plan plan = Plan.getForPieceId(pieceId);
            if( plan != null ) {
                goalTextView.setText(plan.getGoal());
                formatGoalTextView(plan);
                setPieceLabelColor(plan);
            }

            int ymd = startOfWeek;
            int today =  MyDate.today();
            int today_num = MyDate.dayOfWeek(today) - 1;

            for (int i = 0; i < 7; i++) {
                int minutes_practiced = Practice.totalDailyPracticeForPiece(pieceId, ymd);
                int minutes_planned = 0;
                if (plan != null) {
                    if (plan.getPracticeOn(i))
                        minutes_planned = plan.getMinutes();
                }

                dayButton[i].setText(createTextForDayButton(ymd, minutes_practiced, minutes_planned, pieceId));

                if (i > today_num || plan == null) {
                    dayButton[i].setTextColor(Color.BLACK);
                } else {
                    if (minutes_practiced == 0 && minutes_planned > 0)
                        dayButton[i].setTextColor(Color.RED);
                }

                // --- Setup button handler
                dayButton[i].setTag(ymd + "," + pieceId);
                dayButton[i].setOnClickListener(new practiceNotesEditSessionButtonHandler());
                ymd = MyDate.nextDay(ymd);
            }

            return true;
        }

        private String createTextForDayButton(int ymd, int mins_practiced, int mins_planned, long pieceId) {
            String totals;

            if (mins_planned > 0)
                totals = String.format("%s/%s",
                        MyTime.MinsToHoursMins(mins_practiced), MyTime.MinsToHoursMins(mins_planned));
            else {
                if (mins_practiced == 0)
                    totals = " ";
                else
                    totals = MyTime.MinsToHoursMins(mins_practiced);
            }
            return totals + "\n" + Note.concatAllNotesForDay(ymd, pieceId);
        }

        private void setPieceLabelColor(Plan plan) {
            pieceNameTextView.setTextColor(Practice.getPracticePieceLabelColor(plan));
        }

        public void formatGoalTextView(Plan plan) {
            if (plan != null) {
                if (plan.getGoal().trim().length() < 1) {
                    goalTextView.setText("No goal");
                    goalTextView.setTextColor(0xffcc0000); // Red
                    goalTextView.setTypeface(null, Typeface.BOLD_ITALIC);
                } else {
                    goalTextView.setText(plan.getGoal());
                    goalTextView.setTextColor(0xff0000cc); // Blue
                    goalTextView.setTypeface(null, Typeface.ITALIC);
                }
            }
        }
    }

    private class practiceNotesEditSessionButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            String ymd_piece = (String)v.getTag();
            String[] fields = ymd_piece.split(",");
            int ymd = Integer.valueOf(fields[0]);
            long pieceid = Long.valueOf(fields[1]);
            openPracticeNotesEditDialog(ymd, pieceid);
        }
    }

    private void openPracticeNotesEditDialog(int ymd,long pieceid) {
        String msg = Note.concatAllNotesForDay(ymd, pieceid);
        int minutes = Practice.totalDailyPracticeForPiece(pieceid, ymd);
        if( minutes == 0 ) {
            Plan plan = Plan.getForPieceId(pieceid);
            if( plan != null )
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
