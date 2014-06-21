package com.tanju.balloon.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Log;

public class Birds {

	public final Bitmap birdBitmap;
	public final int birdBitmapWidth;
	public final int birdBitmapHeight;
	public final int birdTopHeight;
	RectF birdsRect = null;
	public final float VELOCITY = 0.1f;
	//public final int NUMBERofBirds = 10;

	public int number;
	TextPaint numberPaint;
	PointF birdVelocity = new PointF();
	PointF birdPosition = new PointF();

	final GameEngine gameEngine;

	public Birds(GameEngine gameEngine, Bitmap balloon) {
		super();

		this.gameEngine = gameEngine;
		this.birdBitmap = balloon;
		this.birdBitmapWidth = birdBitmap.getWidth();
		this.birdBitmapHeight = birdBitmap.getHeight();
		
		/*numberPaint = new TextPaint();
		numberPaint.setColor(Color.GREEN);
		numberPaint.setTextSize(birdBitmapHeight * 0.4f);*/
		
		birdTopHeight = (int) (birdBitmapHeight * 0.5f);
		
	}

	public void iterate(long millis) {

		birdPosition.x += birdVelocity.x * millis;
		birdPosition.y += birdVelocity.y * millis;

		// ///balonun pozisyonu belirleniyor/////

		// Log.d("asd ",
		// "camera= "+gameEngine.cameraFrame.left+" balon "+gameEngine.balloonPosition.x);

	}

	public void drawBalloon(Canvas canvas) {

		// Log.d("asd ", "camera= "+screenHeight+" "+screenWidth);

		canvas.drawBitmap(birdBitmap, 
				birdPosition.x - gameEngine.cameraFrame.left - birdBitmapWidth / 2,
				birdPosition.y - birdTopHeight / 2, null);
		// canvas.drawText(" " + number, balloonPosition.x
		// - gameEngine.cameraFrame.left - balloonBitmapWidth * 0.4f,
		// balloonPosition.y + balloonTopHeight * 0.2f, numberPaint);

	}

}
