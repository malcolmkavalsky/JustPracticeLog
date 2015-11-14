package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by malcolm on 7/1/15.
 */
public class Progress {
    Activity myParent;
    TextView textViewPlanTime, textViewPracticeTime;
    ProgressBar progressBar;

    Progress(Activity parent) {
        myParent = parent;

        textViewPlanTime = (TextView) parent.findViewById(R.id.textViewPlanTime);
        textViewPracticeTime = (TextView) parent.findViewById(R.id.textViewPracticeTime);
        progressBar = (ProgressBar) parent.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(false);
        update();
    }
    public void update() {
        int planned_minutes = Plan.getTotalPlanTime();

        textViewPlanTime.setText(MyTime.minutesToString(planned_minutes));

        int practiced_minutes = Practice.getTotalPracticeTime(MyDate.today());
        textViewPracticeTime.setText(MyTime.minutesToString(practiced_minutes));

        int percentage;
        if ( planned_minutes > 0 )
            percentage = (practiced_minutes * 100) / planned_minutes;
        else
            percentage = 100;

        progressBar.setProgress(percentage);
        if (percentage < 50)
            progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        else
            progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
    }
}
