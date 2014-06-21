package com.tanju.balloon.game;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class HighScoresActivity extends Activity {

	ImageButton backButton;
	public TextView txt1;
	public TextView txt2;
	public TextView txt3;
	private DatabaseHelper databaseHelper;
	private long counter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.highscores);
		// initialize();
		//defineOnClickEvents();
		
		

		databaseHelper = new DatabaseHelper(this);

		// highscore
		txt1 = (TextView) findViewById(R.id.textView1);

		databaseVeriAl();

		/*
		 * setHighScores();
		 * 
		 * String highscore1 = String.valueOf(highScore[0]); String highscore2 =
		 * String.valueOf(highScore[1]); String highscore3 =
		 * String.valueOf(highScore[2]);
		 * 
		 * CharSequence csHighScore1 = highscore1; CharSequence csHighScore2 =
		 * highscore2; CharSequence csHighScore3 = highscore3;
		 * 
		 * txt1.setTextSize(40f); txt2.setTextSize(40f); txt3.setTextSize(40f);
		 * 
		 * 
		 * txt1.setText( "1. " + csHighScore1); txt2.setText("2. "+
		 * csHighScore2); txt3.setText("3. "+ csHighScore3);
		 */

	}

	private void databaseVeriAl() {
		// TODO Databaseden veri çekmece

		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, new String[] {
				"_id", "isim", "skor" }, null, null, null, null, "skor desc");
		startManagingCursor(cursor);
		StringBuilder builder = new StringBuilder();
		counter = 1;

		while (cursor.moveToNext()) {
			if (counter < 11) {
				// long id = cursor.getLong(cursor.getColumnIndex("_id"));
				String ad = cursor.getString((cursor.getColumnIndex("isim")));
				int skor = cursor.getInt((cursor.getColumnIndex("skor")));

				builder.append(counter).append(". ");
				builder.append(ad).append(": ");
				builder.append(skor).append("\n");
				counter++;
			}
		}
		txt1.setTextSize(20f);
		txt1.setText(builder);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		MenuActivity.highScoreIsActive = false;
		Log.d("DEBUG:", "destroyer a girdi!!!!");
		
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

	/*
	 * public static int [] highScore= new int[3]; public static void
	 * setHighScores(int score){ int temp1; int temp2;
	 * 
	 * if(score> highScore[0]){ temp1 = highScore[0]; highScore[0] = score;
	 * temp2 = highScore[1]; highScore[1]= temp1; temp1 = highScore[2];
	 * highScore[2]= temp2; } else if(score > highScore[1]){ temp2 =
	 * highScore[1]; highScore[1]= score; temp1 = highScore[2]; highScore[2]=
	 * temp2;
	 * 
	 * } else if(score > highScore[2]){ highScore[2]= score; }
	 * 
	 * }
	 */
	private void initialize() {
		backButton = (ImageButton) findViewById(R.id.instructionsMenuBackButton);
	}

	private void defineOnClickEvents() {
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
	}
}
