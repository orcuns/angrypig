package com.tanju.balloon.game;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Stack;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.text.TextPaint;
import android.util.Log;
import android.webkit.WebView.FindListener;
import android.widget.EditText;

@SuppressLint("ParserError")
public class GameEngine  {

	AngryPig angryPigObject;
	AngryPigGameActivity aPGA;

	private Bitmap backgroundBitmap;
	private final Bitmap hillsBitmap;
	private final Bitmap grassBitmap;
	public final Bitmap gameOverBitmap;
	private final Bitmap happyEnd;

	public static Bitmap[] collidedPig;

	public final int grassBitmapHeight;
	public final int hillsBitmapHeight;

	final Random random = new Random();

	Paint kare = new Paint();
	private volatile boolean leftPressed;
	private volatile boolean rightPressed;
	private volatile boolean upPressed;
	private volatile boolean downPressed;

	private boolean vibrationOff = true;
	boolean callHighScoreMethodOnce = true;
	public  boolean gameOverScreenOn = false;

	float changing_X_position;
	public final RectF cameraFrame = new RectF();

	public int NUMBER_OF_BIRDS = 3;
	public int NUMBER_OF_PASTA = 1;

	Stack<Birds> availableBirds = new Stack<Birds>();
	Stack<Birds> currentBirds = new Stack<Birds>();
	
	Stack<Pasta> availablePasta = new Stack<Pasta>();
	Stack<Pasta> currentPasta = new Stack<Pasta>();

	RectF angryPigRect = new RectF();
	RectF birdsRect = new RectF();
	RectF pastaRect = new RectF();
	TextPaint countPaint, textPaintPig, textPaintText;
	
	int pigSizeCounter;

	private DatabaseHelper databaseHelper;

	EditText geUserInput;
	public boolean touchAndLeave = false;
	boolean lastScore = true;
	int life = 4 ;
 
	/*
	 * public GameEngine(Bitmap[] airplane, Bitmap backgroundBitmap, Bitmap
	 * hills, Bitmap grass, Bitmap balloon) { super(); counter = 0;
	 * 
	 * airplaneObject = new Airplane(this, airplane); countPaint = new
	 * TextPaint(); countPaint.setTextSize(30f);
	 * countPaint.setColor(Color.GREEN); countPaint.setAntiAlias(true);
	 * 
	 * caughtBallon = new TextPaint(); caughtBallon.setTextSize(30f);
	 * caughtBallon.setColor(Color.GREEN); caughtBallon.setAntiAlias(true);
	 * 
	 * this.backgroundBitmap = backgroundBitmap; this.hillsBitmap = hills;
	 * this.grassBitmap = grass;
	 * 
	 * this.grassBitmapHeight = grassBitmap.getHeight();
	 * 
	 * Random random2 = new Random(); for (int i = 0; i < NUMBER_OF_BALLOONS;
	 * i++) { availableBalloons.push(new Balloon(this, balloon, random2
	 * .nextInt(10))); } }
	 */

	// CONSTRUCTOR
	public GameEngine(Bitmap angryPig, Bitmap backgroundBitmap, Bitmap hills,
			Bitmap grass, Bitmap balloon, Bitmap pasta, Bitmap sadPig, Bitmap happyEnd,
			DatabaseHelper dh) {  
		super();
		pigSizeCounter = 0;

		angryPigObject = new AngryPig(this, angryPig);
		
		countPaint = new TextPaint();
		countPaint.setTextSize(30f);
		countPaint.setColor(Color.GREEN);
		countPaint.setAntiAlias(true);

		textPaintPig = new TextPaint();
		textPaintPig.setTextSize(30f);
		textPaintPig.setColor(Color.RED);
		textPaintPig.setAntiAlias(true);
		
		textPaintText = new TextPaint();
		textPaintText.setTextSize(30f);
		textPaintText.setColor(Color.GREEN);
		textPaintText.setAntiAlias(true);

		this.backgroundBitmap = backgroundBitmap;
		this.hillsBitmap = hills;
		this.grassBitmap = grass;
		this.gameOverBitmap = sadPig;
		this.happyEnd = happyEnd;

		this.grassBitmapHeight = grassBitmap.getHeight();
		this.hillsBitmapHeight = hillsBitmap.getHeight();

	//	Random random2 = new Random();
		for (int i = 0; i < NUMBER_OF_BIRDS; i++) {
			availableBirds.push(new Birds(this, balloon));
		}
		
		availablePasta.push(new Pasta(this, pasta));

		// AngryPig activity den buraya databasehelperi aktardık böylece
		// activity olusan database buraya geldi.
		databaseHelper = dh;

		
	}
	
	
	public void onLeftKeyPressed() {
		leftPressed = true;
	}

	public void onRightKeyPressed() {
		rightPressed = true;
	}

	public void onUpKeyPressed() {
		upPressed = true;
	}

	public void onDownKeyPressed() {
		downPressed = true;
	}

	public void onLeftKeyReleased() {
		leftPressed = false;
	}

	public void onRightKeyReleased() {
		rightPressed = false;
	}

	public void onDownKeyReleased() {
		downPressed = false;
	}

	public void onUpKeyReleased() {
		upPressed = false;
	}

	public boolean isLeftPressed() {
		return leftPressed;
	}

	public boolean isRightPressed() {
		return rightPressed;
	}

	public boolean isUpPressed() {
		return upPressed;
	}

	public boolean isDownPressed() {
		return downPressed;
	}

	
	private Pasta retrievePasta(){
		Pasta can = availablePasta.pop();
		currentPasta.push(can);
		return can;
	}
	
	private void returnPasta(Pasta pasta){
		currentPasta.remove(pasta);
		availablePasta.add(pasta);
	}
	
	
	private Birds retrieveBalloon() {
		Birds birds = availableBirds.pop();
		currentBirds.push(birds);
		return birds;
	}

	private void returnBalloon(Birds birds) {
		currentBirds.remove(birds);
		availableBirds.add(birds);
	}

	// bottom must be shorter than actual screen so that airplane and balloons
	// don't go all the way to bottom
	public float groundOffset;

	void iterate(long millis) {
		if (currentBirds.size() < NUMBER_OF_BIRDS) {
			Birds birds = retrieveBalloon();

			int n = (int) (groundOffset / birds.birdTopHeight);
			float ah = groundOffset / n;

			// balonun pozisyonları belirleniyor
			birds.birdPosition.x = cameraFrame.right + birds.birdBitmapWidth;
			birds.birdPosition.y = random.nextInt(n) * ah + ah / 2;

			birds.birdVelocity.x = random.nextFloat() * 9f - 0.5f;
			birds.birdVelocity.y = random.nextFloat() * 0.50f - 0.25f;

			birds.iterate(millis);

		}

		ArrayList<Birds> birdsToReturn = new ArrayList<Birds>();

		for (Birds birds : currentBirds) {

			// balloon's RectF for collision
			birdsRect.left = birds.birdPosition.x - (birds.birdBitmapWidth / 2);
			birdsRect.right = birdsRect.left + birds.birdBitmapWidth;
			birdsRect.top = birds.birdPosition.y - (birds.birdBitmapHeight / 2);
			birdsRect.bottom = birdsRect.top + (birds.birdTopHeight * 0.5f);

			// Log.d("asd"+balloon.balloonBitmapHeight,
			// "asd "+balloon.balloonTopHeight);

			// check if there is collision
			if (angryPigRect.intersect(birdsRect)) {
				
				setVibrationOff(false);
				birdsToReturn.add(birds);
				 

				// carpan domuzların büyüklügünü arttırıyor.
				pigSizeCounter++;
				if (pigSizeCounter % 4 != 0) {
					angryPigObject.angryPigBitmap = collidedPig[pigSizeCounter % 4];
					
					life--;
				}
				// GAME OVER EKRANI
				else {
					// game over music baslıyor
					AngryPigGameActivity.gameOver.start();

					gameOverScreenOn = true;
					
					// backgroundBitmap =
					// AngryPigGameActivity.gameOverBackground;
					// AngryPigGameActivity.isFinished = true;
					// drawBackgroundLayer(aPGA.gameOverBackground, canvas,
					// y_axis, drift)
				}

				// domuz kuşa çarpınca çıkan patlama sesi çalışır.
				if (AngryPigGameActivity.explosion != 0)
					AngryPigGameActivity.sp.play(AngryPigGameActivity.explosion, 10, 10, 0, 0, 1);

			}// end of intersect
			
			
		// kuş soldan dışarı çıkarsa
			if (birds.birdPosition.x < cameraFrame.left) {
				birdsToReturn.add(birds);
				escapedBirdsCounter++;
				if (lastScore == true)
					setScore();
			}

		} //end of Birds stack for loop
		
		pastaCollision(millis);
		

		angryPigObject.iterate(millis);

		// airplane RectF
		angryPigRect.left = angryPigObject.angryPigPosition.x	- (angryPigObject.angryPigBitmapWidth / 2);
		angryPigRect.right = angryPigRect.left + angryPigObject.angryPigBitmapWidth;
		angryPigRect.top = angryPigObject.angryPigPosition.y - (angryPigObject.angryPigBitmapHeight / 2);
		angryPigRect.bottom = angryPigRect.top + angryPigObject.angryPigBitmapHeight;

		for (Birds birds : birdsToReturn) {

			returnBalloon(birds);

		}
		
		

	}// end of iterate method

	
	/* can veren pastanın methodu........*/
	private void pastaCollision(long millis) {
		
		if (currentPasta.size() < NUMBER_OF_PASTA) {
			
			Pasta pasta = retrievePasta();
			
			int n = (int) (groundOffset / pasta.pastaTopHeight);
			float ah = groundOffset / n;

			// pasta pozisyonları belirleniyor
			pasta.pastaPosition.x = cameraFrame.right + pasta.pastaBitmapHeight;
			pasta.pastaPosition.y = n * ah + ah / 2;

			pasta.pastaVelocity.x = random.nextFloat() * 9f - 0.5f;
			pasta.pastaVelocity.y = random.nextFloat() * 0.50f - 0.25f;

			pasta.iterate(millis);
		}
		
		ArrayList<Pasta> pastaToReturn = new ArrayList<Pasta>();

		for (Pasta pasta : currentPasta) {
			// pastaların RectF for collision
				pastaRect.left = pasta.pastaPosition.x - (pasta.pastaBitmapWidth / 2);
				pastaRect.right = pastaRect.left + pasta.pastaBitmapWidth;
				pastaRect.top = pasta.pastaPosition.y - (pasta.pastaBitmapHeight / 2);
				pastaRect.bottom = pastaRect.top + (pasta.pastaTopHeight * 0.6f);
			
				
				// check if there is collision
				if (angryPigRect.intersect(pastaRect)) {
					if(life < 4)
					{	life++;
						pigSizeCounter--;
						angryPigObject.angryPigBitmap = collidedPig[pigSizeCounter % 4];
					}
					else
					{  // canı fullken can alırsa +50 puan kazıyor. gameover ekranında kazananmaması içi if koyduk
						if(lastScore == true)
							score+=50;
					}
					
					pastaToReturn.add(pasta);
					//farklı titreşim koy
					//domuz yemek yeme sesi
				}
				
				
				if (pasta.pastaPosition.x < cameraFrame.left) {
					pastaToReturn.add(pasta);
					
				}
				
				
		}// end of pasta loop
		
		//returnPasta stack'ine geri yollamaca
		for (Pasta pasta : pastaToReturn) 
			returnPasta(pasta);
		
		
	}// end of pasta Collision method


	boolean isVibrationOff() {
		return vibrationOff;
	}

	void setVibrationOff(boolean vibrationOff) {
		this.vibrationOff = vibrationOff;

	}

	// static float startTime;
	// boolean birkereGir = true;
	// int tutucu = 1;

	void setScoreWithTimer() {
		/*
		 * Calendar c = Calendar.getInstance(); float minutes =
		 * c.get(Calendar.MINUTE); float seconds = c.get(Calendar.SECOND); float
		 * hours = c.get(Calendar.HOUR); if(birkereGir ==true) { startTime =
		 * hours*60*60 + minutes*60 + seconds; birkereGir=false; }
		 * 
		 * float realTimeSeconds = hours*60*60 + minutes*60 + seconds -
		 * startTime; if(realTimeSeconds == tutucu) { //scoreConstant *=
		 * 1/(counter + 1); //highScore += scoreConstant; if(pigSizeCounter % 3
		 * == 0) score += scoreConstant1; if(pigSizeCounter % 3 == 1) score +=
		 * scoreConstant2; if(pigSizeCounter % 3 == 2) score += scoreConstant3;
		 * tutucu++;
		 * 
		 * }
		 */

	}
	
	
	
	public int getScore(){
		return score;
	}
	public int getLife(){
		return life;
	}
	
    public int score = 0;
	int escapedBirdsCounter = 0;
	final int scoreConstant0 = 5;
	final int scoreConstant1 = 10;
	final int scoreConstant2 = 15;
	final int scoreConstant3 = 20;
	int forLoopCounter = 1;
	void setScore() {
		// sadece kurtulan kuşları skor olarak yazıyor. Pig büyüdükçe aynı
		// oranda daha fazla kuştan kaçtıgı için daha yüksek puan alıyor.
		for (; escapedBirdsCounter == forLoopCounter; forLoopCounter++) {
			if (pigSizeCounter % 4 == 0)
				score += scoreConstant0;
			if (pigSizeCounter % 4 == 1)
				score += scoreConstant1;
			if (pigSizeCounter % 4 == 2)
				score += scoreConstant2;
			if (pigSizeCounter % 4 == 3)
				score += scoreConstant3;

		} // String b = String.valueOf(escapedBirdsCounter); String a =
			// String.valueOf(forLoopCounter); Log.e("SKOOOOR:", b+" ve "+a );
	}

	// /////////////// HER SEYIN ÇIZILDIGI YER ///////////////
	public void draw(Canvas canvas) {

		calculateCameraFrame(canvas);

		drawBackground(canvas);

		if (gameOverScreenOn == false) {
			
			drawBackgroundLayer(backgroundBitmap, canvas, 0, 0.1f);// arkaplaný
																	// basýyor.
			
			drawBackgroundLayer(hillsBitmap, canvas, (int)(cameraFrame.bottom - hillsBitmapHeight + grassBitmapHeight/3) /*270*/, 0.3f);
			// arkaplan
																	// daglarý
																	// basýyor
			// drawBackgroundLayer(grassBitmap, canvas, 200, 1.5f); // çimleri
			// basýyor
			drawBackgroundLayer(grassBitmap, canvas, (int) (cameraFrame.bottom - grassBitmapHeight), 1.5f);// çimleri
																			// basýyor

			// tek tek balonları foreachle stackte dönerek çiziyor.
			for (Birds birds : currentBirds) {
				birds.drawBalloon(canvas);
			}
			
			for(Pasta pasta : currentPasta)
				pasta.drawPasta(canvas);

			angryPigObject.drawAirplane(canvas, cameraFrame); // uçaðý
																// çizdiriyor
																// Airplane
																// classýnda
				

			// canvas.drawText("Ballons: " + counter, canvas.getWidth() * 0.8f, canvas.getHeight() / 7, countPaint);
			// ekrana Skoru bastırıyor
			//canvas.drawText("Score: " + score, canvas.getWidth() * 0.05f, cameraFrame.bottom / 6, countPaint);
			//canvas.drawText("Life: " + life, canvas.getWidth() * 0.05f, cameraFrame.bottom / 8, countPaint);

			setScoreWithTimer();
			// setScore();

		}
		
		// GAME OVER EKRANINI BAS 
		else {
			
			lastScore = false;
			touchAndLeave = true;
			
			/*if (score > 1000)
				drawBackgroundLayer(happyEnd, canvas, 0, 0);
			else*/
				drawBackgroundLayer(gameOverBitmap, canvas, 0, 0);

			// highscore her oyun bitince bir kez cagirilir ve method o skoru
			// database'e kaydeder.
			if (callHighScoreMethodOnce == true) {
				
				setVibrationOff(false);

				SQLiteDatabase db = databaseHelper.getWritableDatabase();
				try {
					ContentValues values = new ContentValues();

					values.put("isim", MenuActivity.playerName);
					values.put("skor", score);
					db.insert(DatabaseHelper.TABLE_NAME, null, values);
					Log.e("DBTEST", "KAYIT EKLENDİ");

				} catch (Exception e) {
					Log.e("DBTEST", e.toString());
				} finally {
					db.close();

				}
				// HighScoresActivity.setHighScores(score);
				callHighScoreMethodOnce = false;	
			}
			else
				setVibrationOff(true);
			
			
			

			AngryPigGameActivity.sp.release();
			AngryPigGameActivity.gameMusic.pause();

		} // end of else gameOver
		
	}// end of draw(Canvas) method

	public void drawBackground(Canvas canvas) {
		canvas.drawColor(Color.BLACK);

	}

	@SuppressLint("FloatMath")
	public void drawBackgroundLayer(Bitmap backgroundBitmap, Canvas canvas,
			int y_axis, float drift) {

		RectF cameraFrame2 = new RectF();

		cameraFrame2.left = cameraFrame.left * drift;
		cameraFrame2.right = cameraFrame2.left + cameraFrame.width();
		cameraFrame2.top = cameraFrame.top;
		cameraFrame2.bottom = cameraFrame.bottom;

		float alpha1 = cameraFrame2.left / backgroundBitmap.getWidth();
		float alpha2 = (cameraFrame2.right + backgroundBitmap.getWidth())
				/ backgroundBitmap.getWidth();

		double alfa1 = Math.floor(alpha1);
		double alfa2 = Math.floor(alpha2);

		for (double i = alfa1; i <= alfa2; i++) {

			changing_X_position = (float) (i * backgroundBitmap.getWidth() - cameraFrame2.left);
			canvas.drawBitmap(backgroundBitmap, changing_X_position, y_axis,
					null);
		}
	}

	private void calculateCameraFrame(Canvas canvas) {

		float planeHorizontalRelativePosition = 1.0f / 4.0f;

		// TODO: calculate planeHorizontalRelativePosition//
		// this part defines borders and makes sure that airplane should fly in
		// these borders

		planeHorizontalRelativePosition = 0.1f
				+ (angryPigObject.angryPigVelocity.x - angryPigObject.MIN_VELOCITY)
				/ (angryPigObject.MAX_VELOCITY - angryPigObject.MIN_VELOCITY)
				* 0.4f;

		// screen moves to right side
		float leftSide;
		float rightSide;

		leftSide = angryPigObject.angryPigPosition.x - canvas.getWidth()
				* planeHorizontalRelativePosition;
		rightSide = leftSide + canvas.getWidth();

		cameraFrame.top = 0;
		cameraFrame.bottom = canvas.getHeight();
		cameraFrame.left = leftSide;
		cameraFrame.right = rightSide;
		groundOffset = cameraFrame.bottom - grassBitmapHeight * 0.9f;
	}

} // end of GameEngine class
