package com.tanju.balloon.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

public class AngryPig {

	public Bitmap angryPigBitmap;
	/*
	 * public Bitmap[] bigPlaneBitmaps; public int airplaneBitmapWidths[];
	 * public int airplaneBitmapHeights[];
	 */
	public final int angryPigBitmapWidth;
	public final int angryPigBitmapHeight;

	PointF angryPigPosition = new PointF();
	PointF angryPigVelocity = new PointF();

	public final float UNIT_ACCELLERATION = 0.0008f;
	public final float UNIT_FRICTION = 1f - 0.01f;
	public final float MAX_VELOCITY = 1.0f;
	public final float MIN_VELOCITY = 0.2f;
	private final float GRAVITY = 0.0001f;

	final GameEngine gameEngine;

	/*
	 * public Airplane(GameEngine gameEngine, Bitmap[] airplanes) { super();
	 * 
	 * this.airplaneBitmapWidth=0; this.airplaneBitmapHeight=0;
	 * this.airplaneBitmap = null; this.gameEngine = gameEngine;
	 * this.bigPlaneBitmaps = airplanes; for(int i = 0; i < airplanes.length;
	 * i++){ this.airplaneBitmapWidths[i] = bigPlaneBitmaps[i].getWidth();
	 * this.airplaneBitmapHeights[i] = bigPlaneBitmaps[i].getHeight(); } }
	 */
	
	
	public AngryPig(GameEngine gameEngine, Bitmap angryPig) {
		super();

		this.gameEngine = gameEngine;
		this.angryPigBitmap = angryPig;
		this.angryPigBitmapWidth = angryPigBitmap.getWidth();
		this.angryPigBitmapHeight = angryPigBitmap.getHeight();

	}

	public void iterate(long millis) {
		if (gameEngine.isLeftPressed()) {
			angryPigVelocity.x -= UNIT_ACCELLERATION * millis;
		}

		if (gameEngine.isUpPressed()) {

			angryPigVelocity.y -= UNIT_ACCELLERATION * millis;
		}
		if (gameEngine.isRightPressed()) {

			angryPigVelocity.x += UNIT_ACCELLERATION * millis;

		}
		if (gameEngine.isDownPressed()) {

			angryPigVelocity.y += UNIT_ACCELLERATION * millis;
		}

		// sürtünme
		angryPigVelocity.x *= (UNIT_FRICTION);
		// yerçekimi
		angryPigVelocity.y += GRAVITY * millis;

		// yatayda hýz limitleri...
		if (angryPigVelocity.x > MAX_VELOCITY) {
			angryPigVelocity.x = MAX_VELOCITY;
		}
		if (angryPigVelocity.x < MIN_VELOCITY) {
			angryPigVelocity.x = MIN_VELOCITY;
		}

		angryPigPosition.x += angryPigVelocity.x * millis;
		angryPigPosition.y += angryPigVelocity.y * millis;

		// airplane position burada yapılıyor
		// : Check min position y 300
		if (gameEngine.groundOffset < angryPigPosition.y) {
			angryPigPosition.y = gameEngine.groundOffset;
			angryPigVelocity.y = 0;
		}

		// : Check max position y 0
		if (gameEngine.cameraFrame.top > angryPigPosition.y) {
			angryPigPosition.y = gameEngine.cameraFrame.top;
			angryPigVelocity.y = 0;
		}
		
		/**if (gameEngine.cameraFrame.right/4 > angryPigPosition.x) {
			angryPigVelocity.x = MAX_VELOCITY;
			
		}
		if (gameEngine.cameraFrame.right/4 < angryPigPosition.x) {
			angryPigPosition.x = gameEngine.cameraFrame.right/4;
		}*/
	}

	public void drawAirplane(Canvas canvas, RectF cameraFrame) {

		final int screenHeight = canvas.getHeight();
		
		float sabitXPosition = (angryPigPosition.x - cameraFrame.left - angryPigBitmapWidth / 2);

		canvas.drawBitmap(angryPigBitmap, sabitXPosition, angryPigPosition.y, null);

	}

}
