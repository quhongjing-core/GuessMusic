package cn.edu.bzu.guessmusic.util;

import android.util.Log;

public class MyLog {
	public static final boolean DEBUG = true;
	
	public static void d(String tag, String message) {
		if (DEBUG) {
			Log.d(tag, message);
		}
	}
	
	public static void w(String tag, String message) {
		if (DEBUG) {
			Log.w(tag, message);
		}
	}
	
	public static void e(String tag, String message) {
		if (DEBUG) {
			Log.e(tag, message);
		}
	}
	
	public static void i(String tag, String message) {
		if (DEBUG) {
			Log.i(tag, message);
		}
	}
}
