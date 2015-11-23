package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Set;


public class PracticeSessionActivity extends Activity {
    Piece currentPiece;
    Plan currentPlan;
    EditText editTextManual;
    EditText editTextGoal;
    EditText editTextNotes;
    PracticeMetronome myMetronome;
    PracticeTimer myTimer;
    PracticeNotes myNotes;
    ImageButton addNoteButton;
    final static String TAG = "Practice";
    long pieceId = 0;
    int elapsedSeconds = 0;
    int secondsRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_session);

        editTextManual = (EditText) findViewById(R.id.editTextManual);
        editTextManual.setCursorVisible(false);

        editTextGoal = (EditText) findViewById(R.id.editTextGoal);
        editTextGoal.setCursorVisible(false);
        editTextNotes = (EditText) findViewById(R.id.editTextNotes);
        editTextNotes.setCursorVisible(false);

        Bundle bundle = getIntent().getExtras();
        Set<String> keys = bundle.keySet();
        // We receive pieceid
        if (keys.contains("pieceid")) {
            pieceId = bundle.getLong("pieceid");
            currentPlan = Plan.getForPieceId(pieceId);
            if (currentPlan != null) {
                editTextGoal.setText(currentPlan.getGoal());
                editTextNotes.setText(currentPlan.getNotes());
                int practiced = Practice.totalDailyPracticeForPiece(pieceId, MyDate.today());
                secondsRemaining = (currentPlan.getMinutes() - practiced) * 60;
                if (secondsRemaining < 0)
                    secondsRemaining = 0;
                editTextManual.setText(Integer.toString(currentPlan.getMinutes()));
            } else {
                secondsRemaining = 900;
                editTextGoal.setText("No goal");
            }
        }

        currentPiece = new Piece(pieceId);

        ((TextView) findViewById(R.id.textViewTitle)).setText(currentPiece.getName());
        Composer composer = new Composer(currentPiece.getComposerId());
        ((TextView) findViewById(R.id.textViewSubtitle)).setText(composer.getName());
    }

    protected void resumeScreen() {
        myMetronome = new PracticeMetronome(this);
        myTimer = new PracticeTimer(this,secondsRemaining, elapsedSeconds );
        myNotes = new PracticeNotes(this, pieceId);

    }

    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        resumeScreen();
    }
    public void onStop() {
        Log.d(TAG, "onStop");
        closeAll();
        super.onStop();
    }

    public void closeAll() {
        myMetronome.close();
        elapsedSeconds = myTimer.getElapsedSeconds();
        secondsRemaining -= elapsedSeconds;
        myTimer.close();
        // myRecorder.close();
        myNotes.close();
    }

    public void backButtonHandler(View v) {
        setResult(Activity.RESULT_CANCELED, null);
        finish();

    }

    public void saveSessionButtonHandler(View v) {

        int ymd = myTimer.getYmd();
        int hms = myTimer.getHms();
        int minutes = myTimer.getElapsedSeconds() / 60;

        if( currentPlan != null ) {
            if (!editTextGoal.getText().toString().equals(currentPlan.getGoal())) {
                currentPlan.setGoal(editTextGoal.getText().toString());
                Plan.update(currentPlan);
            }
            if (!editTextNotes.getText().toString().equals(currentPlan.getNotes())) {
                currentPlan.setNotes(editTextNotes.getText().toString());
                Plan.update(currentPlan);
            }
        }

        Practice.add(currentPiece.getMyid(), minutes, ymd, hms);

        setResult(Activity.RESULT_OK, null);
        finish();

    }

    public void addManualButtonHandler(View v) {
        String mins = editTextManual.getText().toString();
        int minutes = Integer.parseInt(mins);
        myTimer.addMinutes(minutes);
    }


}
