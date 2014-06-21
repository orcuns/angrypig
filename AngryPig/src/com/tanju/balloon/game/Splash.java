package com.tanju.balloon.game;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends Activity {

	MediaPlayer ourSong2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(com.tanju.balloon.game.R.layout.splash);

		ourSong2 = MediaPlayer.create(Splash.this, R.raw.count);
		ourSong2.start();

		Thread timer = new Thread() {
			@Override
			public void run() {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					Intent airplaneIntent = new Intent(Splash.this,
							MenuActivity.class);
					Splash.this.startActivity(airplaneIntent);
				}
			}
		};
		timer.start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ourSong2.release();
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		/*
		 * if (action == MotionEvent.ACTION_DOWN) { Splash.this.finish(); Intent
		 * openningScreenIntent = new Intent(Splash.this, MenuActivity.class);
		 * Splash.this.startActivity(openningScreenIntent); } if (action ==
		 * MotionEvent.ACTION_UP) { finish(); }
		 */
		return true;
	}
}
