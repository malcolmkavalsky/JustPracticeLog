package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;

public class Metronome {
	SoundPool mSoundPool = null;
	int mSoundId;
	int mBpm = 60; // Default
	
	boolean mPlaying = false;
	Activity mParent;
	private Handler mHandler = null;
	Runnable mBackgroundTask = null;
	boolean mClosed;
	boolean mInitialized;

	Metronome(Activity parent) {
		mClosed = false;
		mInitialized = false;
		mParent = parent;
		init();
	}
	
	private void init() {
		if( !mInitialized ) {
			mInitialized = true;
			mClosed = false;
			loadSounds();
			createBackgroundTask();
		}
	}
	public void close() {
		if( !mClosed && mInitialized ) {
			mInitialized = false;
			mClosed = true;
			freeSounds();
			destroyBackgroundTask();
		}
	}
	private void loadSounds() {
		if( mSoundPool == null ) {
			mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
			if( mSoundPool != null) 
				mSoundId = mSoundPool.load(mParent.getApplicationContext(), R.raw.tock, 1);
			else
				Log.e("Metronome", "unable to create SoundPool");
		}
	}
	private void freeSounds() {
		if( mSoundPool != null ) {
			mSoundPool.release();
			mSoundPool = null;
		}
	}
	private void destroyBackgroundTask() {
		stop();
		mHandler = null;
		mBackgroundTask = null;
	}
	private void createBackgroundTask() {
		mHandler = new Handler();
		mBackgroundTask = new Runnable() {
			public void run() {
				if( mSoundPool != null )
					mSoundPool.play(mSoundId, 1f, 1f, 1, 0, 1.0f);
				mHandler.postDelayed(mBackgroundTask, bpmToMilliseconds(mBpm));
			}
		};		
	}

	public void finalize() {
		// Defensive programming: Just in case I forgot to call close
		if( !mClosed ) {
			Log.e("Metronome", "Finalized without closing, call close!");
			close();
		}
	}
	
	public void start() {
		init();
		if( mBackgroundTask != null )
			mBackgroundTask.run(); 
		else
			Log.e("Metronome", "start: no background task");
	}
	public void stop() {
		if( mHandler != null ) 
			mHandler.removeCallbacks(mBackgroundTask);
	}
	public void setBpm(int bpm) {
		mBpm = bpm;		
	}
	public int getBpm() {
		return mBpm;
	}
	private int bpmToMilliseconds(int bpm) {
		int ms = (int)(60000.0f / bpm);
		return ms;
	}
}
