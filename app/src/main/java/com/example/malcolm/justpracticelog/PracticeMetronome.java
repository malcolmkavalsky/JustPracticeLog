package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class PracticeMetronome implements SeekBar.OnSeekBarChangeListener {

   TextView title;
   ImageButton playButton;
   SeekBar mySeekBar;

   boolean playing = false;
   int currentNotch = 10;
   int currentBpm;
   final int notches[] = {
           40,  42,   44,  46,  48,  50,  52,  54,  56,  58,
           60,	 63,   66,  69,  72,  76,  80,  84,  88,  92,
           96, 100,  104, 108, 112, 116, 120, 126, 132, 138,
          144, 152,  160, 168, 176, 184, 192, 200, 208 };

   Metronome metronome;

   PracticeMetronome(Activity parent) {
       currentBpm = notches[currentNotch];
       metronome = new Metronome(parent);
       metronome.setBpm(currentBpm);

       title = (TextView)parent.findViewById(R.id.metronomeTitle);
       playButton = (ImageButton)parent.findViewById(R.id.playButton);
       playButton.setOnClickListener(new playToggle());

       ImageButton b = (ImageButton)parent.findViewById(R.id.plusButton);
       b.setOnClickListener(new plusHandler());

       b = (ImageButton)parent.findViewById(R.id.minusButton);
       b.setOnClickListener(new minusHandler());
       mySeekBar = (SeekBar)parent.findViewById(R.id.seekBar);
       mySeekBar.setOnSeekBarChangeListener(this);
       mySeekBar.setMax(38);
       updateTitle();
       updateSeekBar();
   }

   private class playToggle implements View.OnClickListener
   {
       public void onClick(View v)
       {
           changeState();
       }
   }
   private class plusHandler implements View.OnClickListener
   {
       public void onClick(View v)
       {
           upBpm();
       }
   }
   private class minusHandler implements View.OnClickListener
   {
       public void onClick(View v)
       {
           downBpm();
       }
   }

   private void changeState() {
       playing = !playing;
       if (playing) {
           metronome.start();
           playButton.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            metronome.stop();
           playButton.setImageResource(android.R.drawable.ic_media_play);
       }
   }
   public void close() {
       metronome.close();
   }

   @Override
   public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
       int progress = arg0.getProgress();
       currentNotch = progress;
       currentBpm = notches[currentNotch];
       metronome.setBpm(currentBpm);
       updateTitle();
   }
   public String getBpmName(int bpm) {
       if( bpm < 60 )
           return "Largo";
       if (bpm < 66 )
           return "Larghetto";
       if (bpm < 76 )
           return "Adagio";
       if (bpm < 108 )
           return "Andante";
       if (bpm < 120 )
           return "Moderato";
       if (bpm < 168 )
           return "Allegro";
       if (bpm < 200 )
           return "Presto";
       return "Prestissimo";
   }

   private void downBpm() {
       if( currentNotch > 0) {
           currentNotch--;
           currentBpm = notches[currentNotch];
           updateTitle();
           updateSeekBar();
           metronome.setBpm(currentBpm);
       }
   }

   private void upBpm() {
       if( currentNotch < 38){
           currentNotch++;
           currentBpm = notches[currentNotch];
           updateTitle();
           updateSeekBar();
           metronome.setBpm(currentBpm);
       }
   }

   @Override
   public void onStartTrackingTouch(SeekBar arg0) {
   }

   @Override
   public void onStopTrackingTouch(SeekBar arg0) {
   }
   public void updateTitle() {
       title.setText( Integer.toString(currentBpm) + "  " + getBpmName(currentBpm));
   }
   public void updateSeekBar() {
       mySeekBar.setProgress(currentNotch);
   }

}
