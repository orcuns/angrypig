package com.tanju.balloon.game;

import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity {

	
	public static MediaPlayer menuSong;

	private ImageButton playButton;
	private ImageButton optionsButton;
	private ImageButton instructionsButton;
	private ImageButton highScoresButton;

	public static boolean tiltMode = false;
	
	public static boolean gameIsActive = false;
	public static boolean optionsIsActive = false;
	public static boolean instructionIsActive = false;
	public static boolean highScoreIsActive = false;

	public Context context;
	CharSequence text1;
	CharSequence text2;
	CharSequence text3;
	int duration;
	Toast toast1, toast2, toast3;

	// public EditText isimText;
	public static String playerName;
	
	AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.menu);
		initialize();

		menuSong = MediaPlayer.create(MenuActivity.this, R.raw.ewandobson_time);

		//menuSong.start();
		menuSong.setLooping(true);

		defineOnClickEvents();

		// context = getApplicationContext();
		context = this;
		text1 = "Music Muted!";
		text2 = "Music ON!";
		text3 = "Please use English only Characters";
		duration = Toast.LENGTH_SHORT;

	}
	
	public static void pauseMenuMusic(){
		
		menuSong.pause();
	}
	
	public static void resumeMenuMusic(){
		
		menuSong.start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/*
		 * menuSong = MediaPlayer.create(MenuActivity.this, R.raw.menusong);
		 * menuSong.start(); menuSong.setLooping(true);
		 */
		
		 menuSong.start();
		
		Log.e("log", "resume");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if(optionsIsActive == true || instructionIsActive == true || highScoreIsActive == true){
		
		}
		else if(gameIsActive == true)
			menuSong.pause();
		
		else
			menuSong.pause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		menuSong.release();
	}

	// menuSong'u kapatmak için keyDown methodunu override ettim
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (KeyEvent.KEYCODE_BACK == keyCode) {

				//menuSong.release();

				finish();
			}

			if (KeyEvent.KEYCODE_VOLUME_DOWN == keyCode) {
				toast1 = Toast.makeText(context, text1, duration);
				menuSong.setVolume(0, 0);
				toast1.show();
			}
			if (KeyEvent.KEYCODE_VOLUME_UP == keyCode) {
				toast2 = Toast.makeText(context, text2, duration);
				menuSong.setVolume(1, 1);
				toast2.show();
			}
			break;
			
		case KeyEvent.KEYCODE_BACK:
		/*	if (menuSong.isPlaying()) {
				menuSong.stop();
			}
			menuSong.release();*/
			finish();
			break;
			
		}
		return true;
	}

	// button initialization
	private void initialize() {
		playButton = (ImageButton) findViewById(R.id.menuPlayButton);
		optionsButton = (ImageButton) findViewById(R.id.menuOptionsButton);
		instructionsButton = (ImageButton) findViewById(R.id.menuInstructionsButton);
		highScoresButton = (ImageButton) findViewById(R.id.menuHighScoresButton);

	}

	private void defineOnClickEvents() {


		playButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.nickname, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);

				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);
				

				// set dialog message
				alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog,int id) {// "[a-zA-Z]+" ,// "^[a-zA-Z0-9]*$"
						if (Pattern.matches("[a-zA-Z]+",userInput.getText().toString())) {

					playerName = userInput.getText().toString();

					Intent playGame = new Intent(MenuActivity.this, AngryPigGameActivity.class);
					MenuActivity.this.startActivity(playGame);
					
					gameIsActive = true;
											
					} 
						else {
								toast3 = Toast.makeText(context, text3, duration);
								toast3.show();
							}
				}

								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});

				// create alert dialog
			    alertDialog = alertDialogBuilder.create();
					
		   alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					        	
				// show it
				alertDialog.show();
				
				

			}
		});
		
	
		highScoresButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent highscore = new Intent(MenuActivity.this, HighScoresActivity.class);
				MenuActivity.this.startActivity(highscore);

				highScoreIsActive = true;
			}
		});
		

		optionsButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent openOptionsScreen = new Intent(MenuActivity.this,
						OptionsActivity.class);
				MenuActivity.this.startActivity(openOptionsScreen);

				// optionstan tilt true mu false mu check etmeli ve bu classa
				// aktarmalý
				// OptionsActivity OA = new OptionsActivity();
				tiltMode = OptionsActivity.tiltModisON;
				
				optionsIsActive= true;

			}
		});

		instructionsButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent openInstructions = new Intent(MenuActivity.this,
						InstructionsActivity.class);
				MenuActivity.this.startActivity(openInstructions);
				
				instructionIsActive= true;
				
			}
		});
		

	
	
	}// end of defineOnClickEvents() method
}
