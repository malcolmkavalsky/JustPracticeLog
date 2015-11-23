package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;


public class PlanTabletActivity extends Activity {
    Shortcuts shortcuts;
    List<Plan> model;
    PlanHolder[] holder;
    int planrows = 0;
    int currentRow = -1;
    PieceChooserDialog pieceChooserDialog;
    Dialog dlg;
    final String TAG = "PlanTabletActivity";

    private PieceChooserListener mListener =
            new PieceChooserListener() {
                public void chose(long pieceid) {
                    setCurrentPiece(pieceid);
                }
                public void cancelled() {
                }
                public void all() {
                }
                public void add() {
                }
                public void delete() {
                    deleteCurrentPiece();
                }
            };
    // --- Time Chooser ---
    TimeChooserDialog timeChooserDialog;
    private void rowChooseTime(PlanHolder ph) {
        currentRow = getRowOfPlanHolder(ph);
        timeChooserDialog.open();
    }
    private TimeChooseListener tListener =
            new TimeChooseListener() {
                public void chose(String result) {
                    if( result != null)
                        setCurrentTime(result);
                }
            };
    private void setCurrentTime(String hhmm) {
        PlanHolder ph = holder[currentRow];
        int minutes = MyTime.HoursMinsToMins(hhmm);
        ph.minutes.setText(MyTime.MinsToHoursMins(minutes));
        ph.plan.setMinutes(minutes);
        if( ph.plan.getPieceId() != 0)
            Plan.update(ph.plan);
        updateTotals();
    }
    // --- Priority Chooser ---
    PriorityChooserDialog priorityChooserDialog;
    private void rowChoosePriority(PlanHolder ph) {
        currentRow = getRowOfPlanHolder(ph);
        priorityChooserDialog.open();
    }
    private PriorityChooserListener pListener =
            new PriorityChooserListener() {
                public void chose(int result) {
                        setCurrentPriority(result);
                }
                public void cancelled() {

                }
            };
    private void setCurrentPriority(int newPriority) {
        PlanHolder ph = holder[currentRow];
        int oldPriority = ph.plan.getPriority();
        for( Plan item: Plan.getAllPlans()) { // Sorted by priority
            int priority = item.getPriority();
            if( newPriority < oldPriority) {
                if ( priority >= newPriority && priority < oldPriority ) {
                    item.setPriority(priority + 1);
                    Plan.update(item);
                }

            } else {
                if ( priority > oldPriority && priority <= newPriority ) {
                    item.setPriority(priority - 1);
                    Plan.update(item);
                }
            }
        }
        ph.plan.setPriority(newPriority);
        if( ph.plan.getPieceId() != 0)
            Plan.update(ph.plan);
        reloadTable();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_plan_tablet);
        holder = new PlanHolder[50];

        pieceChooserDialog = new PieceChooserDialog(this);
        pieceChooserDialog.setListener(mListener);
        pieceChooserDialog.enableAdd();
        pieceChooserDialog.enableDelete();

        priorityChooserDialog = new PriorityChooserDialog(this);
        priorityChooserDialog.setListener(pListener);

        timeChooserDialog = new TimeChooserDialog(this,0,15);
        timeChooserDialog.setTimeChooseListener(tListener);


        shortcuts = new Shortcuts(this, 2);
        reloadTable();
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }


    public void reloadTable() {
        Log.d(TAG,"reloadTable");

        TableLayout table = (TableLayout) findViewById(R.id.TableLayout01);
        table.removeAllViews();
        loadPlan();
        updateTotals();
    }
    public void loadPlan() {
        model = Plan.getAllPlans();
//        removeInvalidItemsFromModel(); // Defensive programming (deleted piece that was in plan)
        int priority = 1;
        planrows = 0;
        for( Plan item : model) {
            item.setPriority(priority);
            addRow(item);
            priority++;
        }

        Plan plan = new Plan();
        plan.setMyid(0L);
        plan.setPriority(priority);
        plan.setMinutes(15);
        addRow(plan);
    }
    private void setCurrentPiece(long pieceid) {
        Piece piece = new Piece(pieceid);
        Composer composer = new Composer(piece.getComposerId());

        PlanHolder ph = holder[currentRow];
        ph.piece.setText(piece.getName());
        ph.composer.setText(composer.getName());
        ph.plan.setPieceId(pieceid);
        if( ph.plan.getMyid() == 0) {
            ph.plan.setMyid(Database.generateUniqueId());
            Plan.add(ph.plan);
            reloadTable();
        } else
            Plan.update(ph.plan);
    }
    private void deleteCurrentPiece() {
        PlanHolder ph = holder[currentRow];
        Plan.delete(ph.plan.getMyid());
        reloadTable();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plan_tablet, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public boolean addRow(Plan plan) {

        TableLayout table = (TableLayout) findViewById(R.id.TableLayout01);

        LayoutInflater inflater=getLayoutInflater();
        View row=inflater.inflate(R.layout.activity_plan_tablet_row,table, false);
        findViewById(R.id.TableLayout01);
        holder[planrows] = new PlanHolder(row);
        if( holder[planrows].populate(plan) == false )
            return false;
        for(int i=0;i<7;i++)
            holder[planrows].box[i].setOnClickListener(new checkBoxHandler());
        table.addView(row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

         // Connect row's "Choose piece/composer" button
        TextView b = (TextView)(row.findViewById(R.id.piece));
        b.setTag(holder[planrows]);
        b.setOnClickListener(new rowChoosePieceButtonHandler());
        b = (TextView)(row.findViewById(R.id.composer));
        b.setTag(holder[planrows]);
        b.setOnClickListener(new rowChoosePieceButtonHandler());

        // Connect row's "Choose goal" button
        b = (TextView)(row.findViewById(R.id.goal));
        b.setTag(holder[planrows]);
        b.setOnClickListener(new rowChooseGoalButtonHandler());

        // Connect row's "Priority" button
        b = (Button)(row.findViewById(R.id.priority));
        b.setTag(holder[planrows]);
        b.setOnClickListener(new rowChoosePriorityButtonHandler());

        // Connect row's "Minutes" button
        b = (Button)(row.findViewById(R.id.minutes));
        b.setTag(holder[planrows]);
        b.setOnClickListener(new rowChooseTimeButtonHandler());

        planrows++;
        return true;
    }
     private class rowChoosePriorityButtonHandler implements View.OnClickListener    {
        public void onClick(View v) {
            rowChoosePriority((PlanHolder) v.getTag());
        }
    }
    private class rowChooseTimeButtonHandler implements View.OnClickListener    {
        public void onClick(View v) {
            rowChooseTime( (PlanHolder)v.getTag());
        }
    }
    private class rowChoosePieceButtonHandler implements View.OnClickListener    {
        public void onClick(View v) {
            rowChoosePiece( (PlanHolder)v.getTag());
        }
    }
    private class rowChooseGoalButtonHandler implements View.OnClickListener    {
        public void onClick(View v) {
            rowChooseGoal( (PlanHolder)v.getTag());
        }
    }
    private void rowChoosePiece(PlanHolder ph) {
        currentRow = getRowOfPlanHolder(ph);
        pieceChooserDialog.open();
    }
    // --- Goal Chooser uses Dialog alert ---
    private void rowChooseGoal(PlanHolder ph) {
        currentRow = getRowOfPlanHolder(ph);
        dlg = openAlertDialog(ph.goal.getText().toString());
        dlg.show();
    }
    private void setCurrentGoal(String goal) {
        PlanHolder ph = holder[currentRow];
        ph.goal.setText(goal);
        if ( goal.isEmpty())
            ph.goal.setTextColor(0xffff0000);
        else
            ph.goal.setTextColor(0xff0000ff);

        ph.plan.setGoal(goal);
        if( ph.plan.getPieceId() != 0)
            Plan.update(ph.plan);
    }

    private Dialog openAlertDialog(String note) {
        LayoutInflater factory = LayoutInflater.from(this);

        final View textEntryView = factory.inflate(R.layout.goalentrydialog, null);

        final EditText noteText = (EditText)textEntryView.findViewById(R.id.goalEditText);
        noteText.setText(note);

        return new AlertDialog.Builder(this)
                .setView(textEntryView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        setCurrentGoal(noteText.getText().toString());
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        setCurrentGoal("");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
    }

    private int getRowOfPlanHolder(PlanHolder ph) {
        int rownum = -1;
        for(int i=0;i<planrows;i++) {
            if( holder[i] == ph ) {
                rownum = i;
                break;
            }
        }
        return rownum;

    }


    static class PlanHolder {
        private Plan plan;
        private Button priority=null;
        private TextView piece=null;
        private TextView composer=null;
        private TextView goal=null;
        private Button minutes=null;
        private TextView total=null;
        private CheckBox[] box;
//		private int planid;

        PlanHolder(View row) {
            priority=(Button)row.findViewById(R.id.priority);
            piece=(TextView)row.findViewById(R.id.piece);
            composer=(TextView)row.findViewById(R.id.composer);
            goal=(TextView)row.findViewById(R.id.goal);
            minutes=(Button)row.findViewById(R.id.minutes);
            box = new CheckBox[7];
            box[0] =(CheckBox)row.findViewById(R.id.box0);
            box[1] =(CheckBox)row.findViewById(R.id.box1);
            box[2]=(CheckBox)row.findViewById(R.id.box2);
            box[3]=(CheckBox)row.findViewById(R.id.box3);
            box[4]=(CheckBox)row.findViewById(R.id.box4);
            box[5]=(CheckBox)row.findViewById(R.id.box5);
            box[6]=(CheckBox)row.findViewById(R.id.box6);

        }
        boolean isChecked( char [] days, int index) {
            if( days[index] == '_')
                return false;
            else
                return true;
        }

        boolean populate(Plan plan) {
            Log.d("PlanTabletActivity","populate:" + plan.toString());

            this.plan = plan;
            if (plan.getMyid() != 0) {
                this.priority.setText(Integer.toString(plan.getPriority()));
                Piece piece = new Piece(plan.getPieceId());
                this.piece.setText(piece.getName());
                Composer composer = new Composer(piece.getComposerId());
                if (composer != null)
                    this.composer.setText(composer.getName());
                if (plan.getGoal().trim().isEmpty()) {
                    this.goal.setText("No goal ...");
                    this.goal.setTextColor(0xffff0000);
                } else {
                    this.goal.setText(plan.getGoal());
                    this.goal.setTextColor(0xff0000ff);
                }
                this.minutes.setText(MyTime.MinsToHoursMins(plan.getMinutes()));
                char[] adays = plan.getDays().toCharArray();
                Log.d("PlanTabletActivity", plan.getDays());

                for (int i = 0; i < 7; i++) {
                    box[i].setChecked(isChecked(adays, i));
                                    }
            } else { // last dummy row
                this.priority.setText(Integer.toString(plan.getPriority()));
                this.piece.setText(null);
                this.composer.setText("+ Add new piece ...");
                this.composer.setTextSize(20.0f);
                this.goal.setText(null);
                this.minutes.setText(MyTime.MinsToHoursMins(plan.getMinutes()));
                for (int i = 0; i < 7; i++)
                    box[i].setChecked(false);
            }
            return true;
        }
    }
    private class checkBoxHandler implements View.OnClickListener    {
        public void onClick(View v)	{
            checkBoxClicked();
        }
    }
    public void checkBoxClicked() {
        updateTotals();
    }

    public void updateTotals() {
        int[] dayTotal = new int[7];
        int[] rowTotal = new int[planrows];

        for( int j=0; j<7; j++) {
            dayTotal[j] = 0;
            for(int i=0;i<planrows;i++) {
                if( holder[i].box[j].isChecked() ) {
                    dayTotal[j] += MyTime.HoursMinsToMins(holder[i].minutes.getText().toString());
                }
            }
        }
        int grandTotal = 0;
        for(int row=0;row<planrows;row++) {
            rowTotal[row] = 0;
            for( int day=0; day<7; day++) {
                if( holder[row].box[day].isChecked() ) {
                    int mins = MyTime.HoursMinsToMins(holder[row].minutes.getText().toString());
                    rowTotal[row] += mins;
                    grandTotal += mins;
                }
            }
        }
        for(int i=0;i<7;i++) {
            String textViewID = String.format("dayTotal%d",i);
            int resID = getResources().getIdentifier(textViewID, "id", "com.example.malcolm.justpracticelog");
            TextView tv = ((TextView) findViewById(resID));
            tv.setText(MyTime.MinsToHoursMins(dayTotal[i]));
        }

        // Update database with all plan items (each row) that has been changed
        for(int row=0;row<planrows;row++) {
            Plan plan = holder[row].plan; // Original plan
            if( plan.getPieceId() != 0 ) { // Must choose some piece
                boolean dirty = false;
                for(int day=0;day<7;day++) {
                    if( plan.getPracticeOn(day) != holder[row].box[day].isChecked()) {
                        plan.setPracticeOn(day, holder[row].box[day].isChecked());
                        dirty = true;
                    }
                }
                if( dirty )
                    Plan.update(plan);
            }
        }
    }
}
