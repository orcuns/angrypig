package com.tanju.balloon.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;

public class Pasta {
	
	final GameEngine gameEngine;
	public final Bitmap pastaBitmap;
	public final int pastaBitmapWidth;
	public final int pastaBitmapHeight;
	public final int pastaTopHeight;
	
	PointF pastaVelocity = new PointF();
	PointF pastaPosition = new PointF();
	
	public Pasta(GameEngine gameEngine, Bitmap pastaBitmap){
		
		this.gameEngine = gameEngine;
		this.pastaBitmap = pastaBitmap;
		this.pastaBitmapWidth = pastaBitmap.getWidth();
		this.pastaBitmapHeight = pastaBitmap.getHeight();
		
		pastaTopHeight = (int) (pastaBitmapHeight * 0.6f);
		
	}
	
	public void iterate(long millis) {
		
		pastaPosition.x += pastaVelocity.x * millis;
		pastaPosition.y += pastaVelocity.y * millis;
	}
	
	
	public void drawPasta(Canvas canvas)
	{
		canvas.drawBitmap(pastaBitmap, 
				pastaPosition.x - gameEngine.cameraFrame.left - pastaBitmapWidth / 2,
				pastaPosition.y - pastaTopHeight / 2, null);
	}
}
