package com.tanju.balloon.game;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

public class VibrateService  extends Service {

	 Vibrator v;

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		
		v  = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		
		  
			  v.vibrate(300); //300 ms titreþir
		
	 }
	
	
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		v.cancel();
		//Log.e("destroy", "vibratör");
	}




	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
