package com.tanju.balloon.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GameJoystick {
	
	private Bitmap _joystick;
	private Bitmap _joystickBg;
	private Bitmap _trigger;
	private Bitmap _triggerDown;
	
	public static int joystick_height;
	public static int joystick_width;
	public static int joystickBg_height;
	public static int joystickBg_width; 
	
	public Bitmap get_triggerDown() {
		return _triggerDown;
	}

	public void set_triggerDown(Bitmap triggerDown) {
		_triggerDown = triggerDown;
	}

	public GameJoystick(Resources res){
		
		_joystick = (Bitmap)BitmapFactory.decodeResource(res,R.drawable.joystick);
		_joystickBg = (Bitmap)BitmapFactory.decodeResource(res, R.drawable.joystick_bg);
		
		joystick_height = _joystick.getHeight();
		joystick_width = _joystick.getWidth();
		joystickBg_height = _joystickBg.getHeight();
		joystickBg_width = _joystickBg.getWidth();

	}

	public Bitmap get_joystick() {
		return _joystick;
	}

	public void set_joystick(Bitmap joystick) {
		_joystick = joystick;
	}

	public Bitmap get_joystickBg() {
		return _joystickBg;
	}

	public void set_joystickBg(Bitmap joystickBg) {
		_joystickBg = joystickBg;
	}
	
	public Bitmap get_trigger() {
		return _trigger;
	}

	public void set_trigger(Bitmap trigger) {
		_trigger = trigger;
	}

}
