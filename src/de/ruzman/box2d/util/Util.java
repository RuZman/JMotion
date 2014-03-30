package de.ruzman.box2d.util;

import de.ruzman.leap.LeapApp;

public class Util {	
    public static final float HEIGHT = 100;
    public static final float WIDTH = ((float) LeapApp.getDisplayWidth()/LeapApp.getDisplayHeight()*HEIGHT);
	
	public static float meterToPixelX(float meter) {
		return ((float) LeapApp.getDisplayWidth()) / WIDTH * meter;
	}
	
	public static float meterToPixelY(float meter) {
		return LeapApp.getDisplayHeight() - ((float) LeapApp.getDisplayHeight()) / HEIGHT * meter;
	}
	
	public static float pixelToMeterX(float pixel) {
		return WIDTH * pixel / LeapApp.getDisplayWidth();
	}
	
	public static float pixelToMeterY(float pixel) {
		return HEIGHT * ((LeapApp.getDisplayHeight() - pixel) / LeapApp.getDisplayHeight());
	}
}
