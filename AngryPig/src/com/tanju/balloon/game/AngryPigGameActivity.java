package com.tanju.balloon.game;

import java.util.Calendar;
import com.tanju.balloon.game.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class AngryPigGameActivity extends Activity implements OnTouchListener,
		OnKeyListener, SensorEventListener {
	/** Called when the activity is first created. */
	
	static boolean birkezBitmapleriAl = true;	
	
	public static GameJoystick _joystick;
	public  Point _touchingPoint;
	//public Point _pointerPosition = new Point(220,150);
	private Boolean _dragging = false;
	
	Context context;
	CharSequence text1;
	CharSequence text2;
	int duration;
	Toast toast1;
	Toast toast2;


	public static SoundPool sp;
	public static MediaPlayer mp, gameMusic, gameOver;
	public static int fartPig = 0;
	public static int explosion = 0;

	MyGameClassSurface ourSurfaceView;
	static Bitmap angryPig;
	static Bitmap downarrow;
	static Bitmap leftarrow;
	static Bitmap rightarrow;
	static Bitmap uparrow;
	static Bitmap hills;
	static Bitmap grass;
	static Bitmap birdBitmap;
	static Bitmap gameOVER;
	static Bitmap happyEnd;
	static Bitmap pastaBitmap;

	public static Bitmap backgroundBitmap;
	
	static Bitmap[] collidedAngryPig = new Bitmap[4];

	float screenX, screenY;

	int rightArea, upArea, downArea, leftArea;

	public boolean tiltModisON = MenuActivity.tiltMode;

	private DatabaseHelper databaseHelper;

	private volatile long lastRenderTime;
	private GameEngine gameEngine;
	private SensorManager sensorManager;

	 Paint opacityRight = new Paint();
	 Paint opacityLeft = new Paint();
	 Paint opacityUp = new Paint();
	 Paint opacityDown = new Paint();

	// Ekranýn uyku moduna girmemesi için:
	private PowerManager.WakeLock wl;

	//çarpýnca titreþim yaratýr.
//	public Vibrator viber = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
	public Intent intentVibrate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		databaseHelper = new DatabaseHelper(this);

		// Music sesini oyun içinde açma kapama!
		context = getApplicationContext();
		text1 = "Music Muted!";
		text2 = "Music ON!";
		duration = Toast.LENGTH_SHORT;

		// ses dosyalarýný çekmece
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		fartPig = sp.load(this, R.raw.fart, 1);
		explosion = sp.load(this, R.raw.explode, 1);

		gameOver = MediaPlayer.create(AngryPigGameActivity.this, R.raw.gameover);
		gameMusic = MediaPlayer.create(AngryPigGameActivity.this,R.raw.gamemusic);


		//gameMusic.start();
		gameMusic.setLooping(true);

		//Çarpma titreþimi
		intentVibrate =new Intent(getApplicationContext(),VibrateService.class);
		
		
	     	
		// Ekranýn uyku moduna girmemesi için:
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

		// Tam ekran yapmak için bu kodu yazdýk
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/***************************************/

		// sensor Tanýmlanýyor
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		/*********************************************************/

		ourSurfaceView = new MyGameClassSurface(this); // aþagýdaki classý
														// cagirior.
		
		_joystick = new GameJoystick(getResources());
		_touchingPoint = new Point((int)(ourSurfaceView.screenWidth() - ((GameJoystick.joystick_width)*2.22f)),
				 (int)(ourSurfaceView.screenHeight() - ((GameJoystick.joystick_height)*2.22f)));
		
		
		ourSurfaceView.setOnTouchListener(this);
		ourSurfaceView.setOnKeyListener(this);

		/** *****************************************/
		
if(birkezBitmapleriAl == true){
		// dosyadan imageleri alan kodlar
		angryPig = BitmapFactory.decodeResource(getResources(),
				R.drawable.domuzcuk);
		collidedAngryPig[0] = BitmapFactory.decodeResource(getResources(),
				R.drawable.domuzcuk);
		collidedAngryPig[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.domuzcuk3k);
		collidedAngryPig[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.domuzcuk4k);
		collidedAngryPig[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.domuzcuk5k);
	

		gameOVER = BitmapFactory.decodeResource(getResources(),R.drawable.gameover);
		happyEnd = BitmapFactory.decodeResource(getResources(),R.drawable.happyend);

		downarrow = BitmapFactory.decodeResource(getResources(),R.drawable.downarrowred);
		uparrow = BitmapFactory.decodeResource(getResources(),	R.drawable.uparrowred);
		rightarrow = BitmapFactory.decodeResource(getResources(), R.drawable.rightarrowred);
		leftarrow = BitmapFactory.decodeResource(getResources(), R.drawable.leftarrowred);
		backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sky);
		hills = BitmapFactory.decodeResource(getResources(), R.drawable.hills);
		grass = BitmapFactory.decodeResource(getResources(), R.drawable.grass);
		birdBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bird2t);
		pastaBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.option_off);
		
		birkezBitmapleriAl=false;
		}

		GameEngine.collidedPig = collidedAngryPig;

		gameEngine = new GameEngine(angryPig, backgroundBitmap, hills, grass, birdBitmap,pastaBitmap , gameOVER, happyEnd, databaseHelper);
		setContentView(ourSurfaceView);
		
		
	}


	

	// 2. seferde oyunda çýkmasý için kilit
	boolean islock = true;
	boolean secondLock = true;
	byte kilit = 0;
	int action;

	public boolean onTouch(View v, MotionEvent event) {
		update(event);
		return true;
	}
	

	private MotionEvent lastEvent;
	public void update(MotionEvent event){

		
		if (event == null && lastEvent == null)
		{
			return;
		}else if(event == null && lastEvent != null){
			event = lastEvent;
		}else{
			lastEvent = event;
		}
		//drag drop 
		if ( event.getAction() == MotionEvent.ACTION_DOWN ){
			
			_dragging = true;
			
			//ONE TOUCH SCREEN
		//	gameEngine.onUpKeyPressed();
			
			// 2. seferde oyunda çýkmasý için kilit
			switch (kilit) {
			case 0:
				islock = false;
				kilit = 1;
				break;
			case 1:
				islock = true;
				break;
			}
			
		}
		
		// Gameover ekranýna 2 kere týklayýnca oyunu tekrar çalýstýran kod:
		else if ( event.getAction() == MotionEvent.ACTION_UP){
			_dragging = false;
			
			//ONETOUCH SCREEN
		//	gameEngine.onUpKeyReleased();
			
						if (gameEngine.touchAndLeave == true && islock == true) {
							if (secondLock == false) {
								
								
								finish();
								startActivity(getIntent());
								
							}
							secondLock = false;
						}
		}
		
		//JOYSTICK BURADA ÇALISIYOR!
		  onJoystickChange(event);
		
		
	} // end of update method
	
	
	/***********ESKÝ onTOUCH methodu BURDA OKLARLA ÇALISAN ******************************************/
	/*public boolean onTouch(View v, MotionEvent event) { */ /*

		action = event.getAction();// & MotionEvent.ACTION_MASK;
		// int pointerIndex = (event.getAction() &
		// MotionEvent.ACTION_POINTER_ID_MASK) >>
		// MotionEvent.ACTION_POINTER_ID_SHIFT;
		// int pointerId = event.getPointerId(pointerIndex);

		// ilk parmagýn basýldýgý koordinatlar..
		screenX = event.getX();
		screenY = event.getY();

		// **SAG BOLGE! SOL BOLGE! YUKARI VE ASAGI BOLGELERR
		rightArea = ((ourSurfaceView.getWidth() / 2) + (ourSurfaceView
				.getWidth() / 4));
		leftArea = (ourSurfaceView.getWidth() / 4);
		upArea = (ourSurfaceView.getHeight() / 4);
		downArea = (ourSurfaceView.getHeight() / 4 + ourSurfaceView.getHeight() / 2);

		if (tiltModisON == true)// TODO: tilt aktifse burasi çalismiyor
		{
			if (action == MotionEvent.ACTION_DOWN) {
				if (screenX > rightArea) {
					gameEngine.onRightKeyPressed();
					opacityRight.setAlpha(250);
					// gaz çýkartma sesi gaz verince!!
//					if (fartPig != 0)
//						sp.play(fartPig, 1, 1, 1, 0, 1);
				} else if (screenX < leftArea) {
					gameEngine.onLeftKeyPressed();
					opacityLeft.setAlpha(250);
				}

				if (screenY > downArea) {
					gameEngine.onDownKeyPressed();
					opacityDown.setAlpha(250);
				} else if (screenY < upArea) {
					gameEngine.onUpKeyPressed();
					opacityUp.setAlpha(250);
				}

				// 2. seferde oyunda çýkmasý için kilit
				switch (kilit) {
				case 0:
					islock = false;
					kilit = 1;
					break;
				case 1:
					islock = true;
					break;
				}
			} else if (action == MotionEvent.ACTION_UP) {
				gameEngine.onLeftKeyReleased();
				gameEngine.onRightKeyReleased();
				gameEngine.onUpKeyReleased();
				gameEngine.onDownKeyReleased();

				opacityRight.setAlpha(80);
				opacityLeft.setAlpha(80);
				opacityUp.setAlpha(80);
				opacityDown.setAlpha(80);

				// Gameover ekranýna týklayýnca oyundan çýkmasýný saðlayan kod:
				if (gameEngine.touchAndLeave == true && islock == true) {
					if (secondLock == false) {
						releasedSounds();
						finish();
					}
					secondLock = false;
				}
			}

		} // end of tilt if conditon

		return true;
	}*/ // end of onTouch method

	// Tusalara basýldýgýnda
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			if (tiltModisON == false)// TODO: tilt aktifse burasi çalismiyor
			{
				// sag bölge!!
				if (KeyEvent.KEYCODE_DPAD_RIGHT == keyCode) {
					gameEngine.onRightKeyPressed();
					opacityRight.setAlpha(250);
					// gaz çýkartma sesi gaz verince!!
					if (fartPig != 0)
						sp.play(fartPig, 1, 2, 2, 0, 1);
				}
				if (KeyEvent.KEYCODE_DPAD_LEFT == keyCode) {
					gameEngine.onLeftKeyPressed();
					opacityLeft.setAlpha(250);
				}
				if (KeyEvent.KEYCODE_DPAD_UP == keyCode) {
					gameEngine.onUpKeyPressed();
					opacityUp.setAlpha(250);
				}
				if (KeyEvent.KEYCODE_DPAD_DOWN == keyCode) {
					gameEngine.onDownKeyPressed();
					opacityDown.setAlpha(250);
				}
				if (KeyEvent.KEYCODE_BACK == keyCode) {
					
					finish();
				}
			
				if (KeyEvent.KEYCODE_VOLUME_DOWN == keyCode) {
					toast1 = Toast.makeText(context, text1, duration);
					if(gameEngine.gameOverScreenOn == false)
					 gameMusic.setVolume(0, 0);
					toast1.show();
				}
				if (KeyEvent.KEYCODE_VOLUME_UP == keyCode) {
					toast2 = Toast.makeText(context, text2, duration);
					if(gameEngine.gameOverScreenOn == false)
					 gameMusic.setVolume(1, 1);
					toast2.show();
				}
				break;

			}
		case KeyEvent.KEYCODE_BACK:
			
			
			finish();
			break;
		
		}

		return true;
	}

	// Tusa basým býrakýldýgýnda Release kodlarý çalýþýr
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (KeyEvent.KEYCODE_DPAD_RIGHT == keyCode) {
			gameEngine.onRightKeyReleased();
			opacityRight.setAlpha(80);
		}
		if (KeyEvent.KEYCODE_DPAD_LEFT == keyCode) {
			gameEngine.onLeftKeyReleased();
			opacityLeft.setAlpha(80);
		}
		if (KeyEvent.KEYCODE_DPAD_UP == keyCode) {
			gameEngine.onUpKeyReleased();
			opacityUp.setAlpha(80);
		}
		if (KeyEvent.KEYCODE_DPAD_DOWN == keyCode) {
			gameEngine.onDownKeyReleased();
			opacityDown.setAlpha(80);
		}
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			
			finish();
		}
	

		return true;
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// wakelock
		wl.release();
		
		if(gameEngine.gameOverScreenOn == false)
			gameMusic.pause();
		else
			gameOver.pause();
		
		stopService(intentVibrate); Log.e("halo", "pause ettin!!");
		ourSurfaceView.pause();
		//finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// wakelock
		wl.acquire();
		
		// gameMusic burda baþlýyor
		if(gameEngine.gameOverScreenOn == false)
			gameMusic.start();
		else
			gameOver.start();
		
		ourSurfaceView.resume(); Log.e("halo", "resume ettin!!");
	}
	
		
	  @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		MenuActivity.gameIsActive = false;
		Log.e("halo", "destroy ettin!!");
	}


	void setViberation(){ 
		  if(gameEngine.isVibrationOff()== false) 
		  {  
			  startService(intentVibrate);
			  gameEngine.setVibrationOff(true);
			 // Log.e("Titre", "titredi");
		  }
	  }
	 
	/**
	 * Diger CLASS BURADA!
	 * 
	 * 
	 * 
	 * -----------------------------------------
	 */

	public  class MyGameClassSurface extends SurfaceView implements Runnable {

		SurfaceHolder ourHolder;
		Thread ourThread = null;
		boolean isRunning = false;
		Canvas canvas;
		public    int screenW;
		public   int screenH;
		
		// COnstructor
		public MyGameClassSurface(Context context) {
			super(context);

			ourHolder = getHolder();
			
			 
		}

		public void pause() {

			isRunning = false;

			if (ourThread != null) {
				Thread moribund = ourThread;
				ourThread = null;
				moribund.interrupt();
			}
			/*
			 * synchronized (mPauseLock) { mPaused = true; }
			 */
		}

		public void resume() {
			isRunning = true;
			ourThread = new Thread(this);
			ourThread.start(); // bu run() methodunu tetikliyor.

			/*
			 * synchronized (mPauseLock) { mPaused = false;
			 * mPauseLock.notifyAll(); }
			 */
		}

		public void run() {

			while (isRunning) { 
				// Yuzey etkin mi degil mi diye bu var
				if (!ourHolder.getSurface().isValid()) {
					continue;
				}

				// sistem suan ki zamaný alýyor ve son render edilen zmandan
				// çýkarýyor ve iteratora gönderiyor.
				long currentTimeMillis = System.currentTimeMillis();
				if (isRunning && lastRenderTime > 0) {
					gameEngine.iterate(currentTimeMillis - lastRenderTime);

				}
				lastRenderTime = currentTimeMillis;

				canvas = ourHolder.lockCanvas();

				gameEngine.draw(canvas);// arkaplanlarý ve uçagý basýyor..
	//			 drawButtons(canvas);// butonlarý basýyor..

				
				
				//draw joystick when game is not over 
				if(gameEngine.gameOverScreenOn == false){ 
				canvas.drawText("Score: " + gameEngine.getScore(), screenW * 0.8f, screenH/ 9, gameEngine.countPaint);
				canvas.drawText("Life: " + gameEngine.getLife(), screenW * 0.8f, screenH / 6, gameEngine.countPaint);		
			
				//draw the joystick background
		/**		canvas.drawBitmap(_joystick.get_joystickBg(), screenW - (GameJoystick.joystickBg_width)*1.5f,
						screenH - (GameJoystick.joystickBg_height)*1.5f, null);
				
				//draw the dragable joystick
				canvas.drawBitmap(_joystick.get_joystick(),_touchingPoint.x , _touchingPoint.y , null);                */
				}
				else{
					// skoru bitis ekranýnda bastýrýyor:
					canvas.drawText("Your Score is: " + gameEngine.getScore(),
							screenW * 0.3f, screenH / 8, gameEngine.textPaintPig);
					// olayý anlatmaca
					canvas.drawText("Double Click To Play Again! Back Button to Quit!",
							screenW * 0.1f, screenH / 5, gameEngine.textPaintText);
				}
		
				ourHolder.unlockCanvasAndPost(canvas);
				
				setViberation();
			}// end of loop while
	
		}// end of run() method

		private void drawButtons(Canvas canvas) {
	canvas.drawBitmap(rightarrow, screenW - 20- (rightarrow.getWidth() / 2), screenH / 2 - (rightarrow.getWidth() / 2), opacityRight);
			canvas.drawBitmap(leftarrow, getLeft() + 20
					- (leftarrow.getWidth() / 2),
					screenH / 2 - (leftarrow.getWidth() / 2), opacityLeft);
			canvas.drawBitmap(downarrow, screenW / 2
					- (downarrow.getWidth() / 2), screenH - 70, opacityDown);
			canvas.drawBitmap(uparrow, screenW / 2 - (uparrow.getWidth() / 2),
					0, opacityUp);

		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			// screenSize input alýyor.
			super.onSizeChanged(w, h, oldw, oldh);

			screenW = w;
			screenH = h;

		}
		
		public int screenWidth(){
			return screenW;		
		}
		public int screenHeight(){
			return screenH;
		}

	} // end of MyGameClassSurface class

	
	/***
	 * JOYSTICK KODLARI
	 * @param event
	 */
	public void onJoystickChange(MotionEvent event){
		
		if ( _dragging ){
			// get the pos
			_touchingPoint.x = (int)event.getX();
			_touchingPoint.y = (int)event.getY();

			// bound to a box
			
		if( _touchingPoint.x < ourSurfaceView.screenWidth() - (GameJoystick.joystickBg_width)*1.5f){
				_touchingPoint.x = (int) ((int) ourSurfaceView.screenWidth() - (GameJoystick.joystickBg_width)*1.5f);
				gameEngine.onLeftKeyPressed();
				
				gameEngine.onRightKeyReleased();
				//gameEngine.onUpKeyReleased();
				//gameEngine.onDownKeyReleased();
			}
		if ( _touchingPoint.x > ourSurfaceView.screenWidth() - (GameJoystick.joystickBg_width)*1.5f + GameJoystick.joystickBg_width/2 ){
		_touchingPoint.x = (int) (ourSurfaceView.screenWidth() - (GameJoystick.joystickBg_width)*1.5f + GameJoystick.joystickBg_width/2);
		gameEngine.onRightKeyPressed();
		gameEngine.onLeftKeyReleased();
		
		//gameEngine.onUpKeyReleased();
		//gameEngine.onDownKeyReleased();
			}
		if (_touchingPoint.y < ourSurfaceView.screenHeight() - (GameJoystick.joystickBg_height)*1.5f){
				_touchingPoint.y = (int) ((int) ourSurfaceView.screenHeight() - (GameJoystick.joystickBg_height)*1.5f);
				gameEngine.onUpKeyPressed();
				//gameEngine.onLeftKeyReleased();
				//gameEngine.onRightKeyReleased();
				
				gameEngine.onDownKeyReleased();
			}
		if ( _touchingPoint.y > ourSurfaceView.screenHeight() - (GameJoystick.joystickBg_height)*1.5f + GameJoystick.joystickBg_height/2 ){
	_touchingPoint.y =  (int) (ourSurfaceView.screenHeight() - (GameJoystick.joystickBg_height)*1.5f + GameJoystick.joystickBg_height/2);
	gameEngine.onDownKeyPressed();
	//gameEngine.onLeftKeyReleased();
	//gameEngine.onRightKeyReleased();
	gameEngine.onUpKeyReleased();
	
			}

			//get the angle
		/*	double angle = Math.atan2(_touchingPoint.y - ((ourSurfaceView.screenHeight() - 
					((GameJoystick.joystick_height)*2.22f))),
					_touchingPoint.x - ((ourSurfaceView.screenWidth() - 
					((GameJoystick.joystick_width)*2.22f)))) 
					/(Math.PI/180);
			*/
			// Move the beetle in proportion to how far 
			// the joystick is dragged from its center
			//_pointerPosition.y += Math.sin(angle*(Math.PI/180))*(_touchingPoint.x/70);
			//_pointerPosition.x += Math.cos(angle*(Math.PI/180))*(_touchingPoint.x/70);

			

		}
		else if (!_dragging)
		{
			// Snap back to center when the joystick is released
			_touchingPoint.x = (int) (ourSurfaceView.screenWidth() - ((GameJoystick.joystick_width)*2.22f));
			_touchingPoint.y = (int) ((ourSurfaceView.screenHeight() - ((GameJoystick.joystick_height)*2.22f)));
			//shaft.alpha = 0;
			gameEngine.onLeftKeyReleased();
			gameEngine.onRightKeyReleased();
			gameEngine.onUpKeyReleased();
			gameEngine.onDownKeyReleased();
			
			
		}
	}
	
	
	
	/**
	 *  SENSOR KODLARI(non-Javadoc)
	 */ //@see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
	 

	public void onSensorChanged(SensorEvent event) {
		// TODO sensor degisince..
		if (tiltModisON == true) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

				// assign directions
				float x = event.values[0];
				float y = event.values[1];
				float z = event.values[2];

				float accelerometerMaxRange = 10; // This is NOT right, but it's
													// a good value to work with
				float newAngleX = 0;
				float newAngleY = 0;
				// float newAngleZ = 0;

				if (z > 9) {
					// Phone is horizontally flat, can't point towards gravity,
					// really. Do whatever you think is right
				} else {
					newAngleX = (x * 90 / accelerometerMaxRange);
					newAngleY = (y * 90 / accelerometerMaxRange);
					// newAngleZ = (float)(z * 90 / accelerometerMaxRange);
				}

				if (newAngleX < 50) {
					gameEngine.onUpKeyPressed();
					opacityUp.setAlpha(250);
				} else {
					gameEngine.onUpKeyReleased();
					opacityUp.setAlpha(80);
				}
				if (newAngleX > 65) {
					gameEngine.onDownKeyPressed();
					opacityDown.setAlpha(250);
				} else {
					gameEngine.onDownKeyReleased();
					opacityDown.setAlpha(80);
				}
				if (newAngleY > 7) {
					gameEngine.onRightKeyPressed();
					opacityRight.setAlpha(250);
					// gaz çýkartma sesi gaz verince!!
					if (fartPig != 0)
						sp.play(fartPig, 1, 1, 1, 0, 1);
				} else {
					gameEngine.onRightKeyReleased();
					opacityRight.setAlpha(80);
				}
				if (newAngleY < -7) {
					gameEngine.onLeftKeyPressed();
					opacityLeft.setAlpha(250);
				} else {
					gameEngine.onLeftKeyReleased();
					opacityLeft.setAlpha(80);
				}

			} // end of if statement
		}// end of true false if statement
	} // end of onSensorChanged

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

} // end of AirplaneGameActivity (main)class 