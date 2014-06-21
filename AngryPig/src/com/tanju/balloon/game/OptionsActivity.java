package com.tanju.balloon.game;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class OptionsActivity extends Activity {
	AudioManager am;
	Vibrator vibe;
	ImageButton soundButton;
	ImageButton animationButton;
	ImageButton vibrationButton;
	ImageButton backButton;
	boolean soundIsMuted = true;
	public static boolean tiltModisON = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		initialize();
		defineOnClickListeners();

	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		MenuActivity.optionsIsActive = false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		MenuActivity.pauseMenuMusic();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		MenuActivity.resumeMenuMusic();
	}
	
	
	private void initialize() {
		soundButton = (ImageButton) findViewById(R.id.optionSoundButton);
		animationButton = (ImageButton) findViewById(R.id.optionAnimationButton);
		vibrationButton = (ImageButton) findViewById(R.id.optionVibrationButton);
		backButton = (ImageButton) findViewById(R.id.optionsMenuBackButton);

		// tiltModisON = true;
	}

	private void defineOnClickListeners() {
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// am.setStreamMute(AudioManager.STREAM_MUSIC, true);
				
				MenuActivity.optionsIsActive = false;
				finish();
			}
		});

		soundButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// am.setStreamMute(AudioManager.STREAM_MUSIC, !soundIsMuted);

				updateAudio();

			}
		});

		animationButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// check animation from airplane game activity' s tiltModeisON

				tiltModisON = updateTilt();

			}
		});

		vibrationButton.setOnClickListener(new OnClickListener() {
			@TargetApi(11)
			public void onClick(View v) {
				// check vibration is on
				// if(vibe.hasVibrator())
				// vibe.cancel();
				// updateImageButtons();
			}
		});
	}

	public void updateVibration() {
		// if(vibe.hasVibrator())
		// vibrationButton.setImageDrawable(getResources().getDrawable(R.drawable.option_on));

	}

	public void updateAudio() {

		if (soundIsMuted == false) {
			soundButton.setImageDrawable(getResources().getDrawable(
					R.drawable.option_on));
			soundIsMuted = true;

		} else {
			soundButton.setImageDrawable(getResources().getDrawable(
					R.drawable.option_off));
			soundIsMuted = false;
		}
	}

	public boolean updateTilt() {

		if (tiltModisON == false) {

			tiltModisON = true;
			MenuActivity.tiltMode = tiltModisON;
			animationButton.setImageDrawable(getResources().getDrawable(
					R.drawable.option_on));
		} else {

			tiltModisON = false;
			MenuActivity.tiltMode = tiltModisON;
			animationButton.setImageDrawable(getResources().getDrawable(
					R.drawable.option_off));
		}

		return tiltModisON;

	}

}
