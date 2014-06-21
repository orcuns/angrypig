package com.tanju.balloon.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class InstructionsActivity extends Activity {
	ImageButton backButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.instructions);
		initialize();
		defineOnClickEvents();
	}

	private void initialize() {
		backButton = (ImageButton) findViewById(R.id.instructionsMenuBackButton);
	}

	private void defineOnClickEvents() {
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				MenuActivity.instructionIsActive = false;
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		MenuActivity.instructionIsActive = false;
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
	
	
	
	
	
	
	
}
