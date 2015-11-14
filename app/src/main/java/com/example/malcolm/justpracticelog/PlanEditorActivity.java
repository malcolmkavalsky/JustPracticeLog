package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class PlanEditorActivity extends Activity {
    EditText editTextGoal;
    EditText editTextNotes;
    EditText editTextPriority;
    EditText editTextMinutes;
    List<CheckBox> day;
    Plan currentPlan;
    int initialPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_editor);

        editTextGoal = (EditText) findViewById(R.id.editTextGoal);
        editTextNotes = (EditText) findViewById(R.id.editTextNotes);
        editTextMinutes = (EditText) findViewById(R.id.editTextMinutes);
        editTextPriority = (EditText) findViewById(R.id.editTextPriority);
        day = new ArrayList<CheckBox>();

        day.add((CheckBox) findViewById(R.id.day0));
        day.add((CheckBox) findViewById(R.id.day1));
        day.add((CheckBox) findViewById(R.id.day2));
        day.add((CheckBox) findViewById(R.id.day3));
        day.add((CheckBox) findViewById(R.id.day4));
        day.add((CheckBox) findViewById(R.id.day5));
        day.add((CheckBox) findViewById(R.id.day6));

        Bundle bundle = getIntent().getExtras();
        currentPlan = new Plan(bundle.getLong("planid"));
        Piece piece = new Piece(currentPlan.getPieceId());
        Composer composer = new Composer(piece.getComposerId());
        ((TextView) findViewById(R.id.textViewTitle)).setText(piece.getName());
        ((TextView) findViewById(R.id.textViewSubtitle)).setText(composer.getName());

        editTextGoal.setText(currentPlan.getGoal());

        editTextPriority.setText(Integer.toString(currentPlan.getPriority()));
        editTextMinutes.setText(Integer.toString(currentPlan.getMinutes()));
        daysToCheckboxes(currentPlan.getDays());

        initialPriority = currentPlan.getPriority();
    }

    public void backButtonHandler(View v) {
        setResult(Activity.RESULT_CANCELED, null);
        finish();
    }

    public void daysToCheckboxes(String s) {
        for (int i = 0; i < 7; i++) {
            if (s.charAt(i) != '_')
                day.get(i).setChecked(true);
            else
                day.get(i).setChecked(false);
        }
    }

    public String CheckboxesToDays() {
        String week = "SMTWTFS";
        char[] s = week.toCharArray();
        for (int i = 0; i < 7; i++) {
            if (!day.get(i).isChecked())
                s[i] = '_';

        }
        return String.valueOf(s);
    }

    public void saveButtonHandler(View v) {
        currentPlan.setGoal(editTextGoal.getText().toString());
        currentPlan.setDays(CheckboxesToDays());
        Plan.update(currentPlan);
        if( currentPlan.getPriority() != initialPriority)
            Plan.changePlanPriority(currentPlan);
        Plan.saveDatabase();
        setResult(Activity.RESULT_OK, null);
        finish();
    }

    public void deleteButtonHandler(View v) {
        Plan.delete(currentPlan);
        setResult(Activity.RESULT_OK, null);
        finish();
    }

    public void incPriorityButtonHandler(View v) {
        currentPlan.setPriority(currentPlan.getPriority() + 1);
        editTextPriority.setText(Integer.toString(currentPlan.getPriority()));
    }

    public void decPriorityButtonHandler(View v) {
        if (currentPlan.getPriority() > 1) currentPlan.setPriority(currentPlan.getPriority() - 1);
        editTextPriority.setText(Integer.toString(currentPlan.getPriority()));
    }

    public void incMinutesButtonHandler(View v) {
        int mins = currentPlan.getMinutes() + 1;
        currentPlan.setMinutes(mins);
        editTextMinutes.setText(Integer.toString(mins));
    }

    public void decMinutesButtonHandler(View v) {
        int mins = currentPlan.getMinutes() - 1;
        if (mins > 0) {
            currentPlan.setMinutes(mins);
            editTextMinutes.setText(Integer.toString(mins));
        }
    }

}
