package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by malcolm on 6/28/15.
 */
public class Shortcuts {
    List<Button> lbuttons;
    Activity myParent;

    Shortcuts(Activity parent, int buttonNumber) {
        myParent = parent;

        lbuttons = new ArrayList<Button>();

        // For regular phones
        Button b = (Button) parent.findViewById(R.id.homeButton);
        int bnum = 0;
        if (b != null) {
            b.setOnClickListener(new homeButtonHandler());
            lbuttons.add(b);
            enable(bnum++);
        }

        b = (Button) parent.findViewById(R.id.piecesButton);
        if (b != null) {
            b.setOnClickListener(new piecesButtonHandler());
            lbuttons.add(b);
            enable(bnum++);
        }

        b = (Button) parent.findViewById(R.id.planButton);
        if (b != null) {
            b.setOnClickListener(new planButtonHandler());
            lbuttons.add(b);
            enable(bnum++);
        }

        b = (Button) parent.findViewById(R.id.practiceButton);
        if (b != null) {
            b.setOnClickListener(new practiceButtonHandler());
            lbuttons.add(b);
            enable(bnum++);
        }

        // For tablets using shortcuts.xml for sw600dp
        b = (Button) parent.findViewById(R.id.homeTabletButton);
        if (b != null) {
            b.setOnClickListener(new homeTabletButtonHandler());
            lbuttons.add(b);
            enable(bnum++);
        }

        b = (Button) parent.findViewById(R.id.piecesTabletButton);
        if (b != null) {
            b.setOnClickListener(new piecesTabletButtonHandler());
            lbuttons.add(b);
            enable(bnum++);
        }

        b = (Button) parent.findViewById(R.id.planTabletButton);
        if (b != null) {
            b.setOnClickListener(new planTabletButtonHandler());
            lbuttons.add(b);
            enable(bnum++);
        }

        b = (Button) parent.findViewById(R.id.practiceTabletButton);
        if (b != null) {
            b.setOnClickListener(new practiceTabletButtonHandler());
            lbuttons.add(b);
            enable(bnum++);
        }
        disable(buttonNumber);
    }

    public void disable(int buttonNumber) {
        Button b = lbuttons.get(buttonNumber);
        b.setTextColor(Color.YELLOW);
        b.setTypeface(null, Typeface.BOLD);
        b.setBackgroundResource(R.drawable.button_border_yellow);
        b.setEnabled(false);
    }
    public void enable(int buttonNumber) {
        Button b = lbuttons.get(buttonNumber);
        b.setTextColor(Color.GRAY);
        b.setTypeface(null, Typeface.NORMAL);
        b.setBackgroundResource(R.drawable.button_border_gray);
        b.setEnabled(true);
    }
    private class homeButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(myParent, MainActivity.class);
            myParent.finish(); // To prevent stacking up Activities,this will remove the history of the parent in the stack
            myParent.startActivityForResult(intent, 0);
        }
    }

    private class homeTabletButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(myParent, MainActivity.class);
            myParent.finish(); // To prevent stacking up Activities,this will remove the history of the parent in the stack
            myParent.startActivityForResult(intent, 0);
        }
    }

    private class piecesButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(myParent, PiecesActivity.class);
            myParent.finish();
            myParent.startActivityForResult(intent, 0);
        }
    }

    private class piecesTabletButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(myParent, PiecesActivity.class);
            myParent.finish();
            myParent.startActivityForResult(intent, 0);
        }
    }

    private class planButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(myParent, PlanListActivity.class);
            myParent.finish();
            myParent.startActivityForResult(intent, 0);
        }
    }

    private class planTabletButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(myParent, PlanTabletActivity.class);
            myParent.finish();
            myParent.startActivityForResult(intent, 0);
        }
    }

    private class practiceButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(myParent, PracticeListActivity.class);
            myParent.finish();
            myParent.startActivityForResult(intent, 0);
        }
    }

    private class practiceTabletButtonHandler implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(myParent, PracticeTabletActivity.class);
            myParent.finish();
            myParent.startActivityForResult(intent, 0);
        }
    }

}
