package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class PracticeTimer extends Timer {

    int timerStartHms;
    int timerStartYmd;
    int elapsedSeconds = 0;
    boolean timerRunning = false;
    int countDown;
    TextView timerElapsedTextView;
    ImageButton timerButton;
    //	ImageButton timerAlarmButton;
    Activity parent;
    MediaPlayer mediaPlayer;
    final static String TAG = "PracticeTimer";

    PracticeTimer(PracticeSessionActivity parent, int startCounter, int elapsedSeconds) {
        Log.d(TAG, "PracticeTimer: startCounter " + startCounter + " elapsedSeconds " + elapsedSeconds);

        this.parent = parent;
        this.elapsedSeconds = elapsedSeconds;
        timerButton = (ImageButton) parent.findViewById(R.id.timerButton);
        timerButton.setOnClickListener(new timerToggle());
//		timerAlarmButton = (ImageButton)parent.findViewById(R.id.timerAlarm);
        timerElapsedTextView = (TextView) parent.findViewById(R.id.textViewTimerElapsed);
//        timerElapsedTextView.setText(MyTime.toString(MyTime.now()));

        timerStartHms = MyTime.now();
        timerStartYmd = MyDate.today();
        timerRunning = true;
        countDown = startCounter;
        mediaPlayer = MediaPlayer.create(parent.getApplicationContext(), R.raw.cuckoo);

        schedule(new TimerTask() {
            @Override
            public void run() {
                timerMethod();
            }
        }, 0, 1000);
    }

    private void timerMethod() {
        parent.runOnUiThread(doSomething);
    }

    private Runnable doSomething = new Runnable() {
        public void run() {
            if (timerRunning) {
                elapsedSeconds++;
                timerElapsedTextView.setText(MyTime.toString(elapsedSeconds));

                if (countDown > 0) {
                    countDown--;
                    if (countDown == 0) {
//						timerAlarmButton.setImageResource(R.drawable.tick);
                        mediaPlayer.start();
                    }
                }
            }
        }
    };

    private class timerToggle implements View.OnClickListener {
        public void onClick(View v) {
            if (timerRunning) {
                timerRunning = false;
                timerButton.setImageResource(R.drawable.next);
            } else {
                timerRunning = true;
                timerButton.setImageResource(R.drawable.pause);
            }
        }
    }

    int getYmd() {
        return timerStartYmd;
    }

    int getHms() {
        return timerStartHms;
    }

    int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public void close() {
        cancel(); // Stop unused_timer
        if (mediaPlayer != null)
            mediaPlayer.release();
        mediaPlayer = null;
    }

    public void addMinutes(int mins) {
        elapsedSeconds += (mins * 60);
    }
}
