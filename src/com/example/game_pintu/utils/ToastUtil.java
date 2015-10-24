package com.example.game_pintu.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	private static Toast mToast;
	private static String mInfo;
	private static long oneTime;
	private static long twoTime;
	
	public static void show (Context context, int resId) {
		show(context, resId, Toast.LENGTH_SHORT);
	}
	public static void showLong (Context context, int resId) {
		show(context, resId, Toast.LENGTH_LONG);
	}
	public static void show (Context context, int resId, int time) {
		String s = context.getResources().getText(resId).toString();
		show(context, s, time);
	}
	
	public static void show (Context context, String info) {
		show(context, info, Toast.LENGTH_SHORT);
	}
	public static void showLong (Context context, String info) {
		show(context, info, Toast.LENGTH_LONG);
	}
	public static void show (Context context, String info, int time) {
		if (mToast == null) {
			mToast = Toast.makeText(context, info, time);
			mToast.show();
			oneTime = System.currentTimeMillis();
		} else {
			twoTime = System.currentTimeMillis();
			if (mInfo.equals(info)) {
				if (twoTime - oneTime > time) {
					mToast.setDuration(time);
					mToast.show();
				}
			} else {
				mToast.setDuration(time);
				mToast.setText(info);
				mToast.show();
			}
		}
		mInfo = info;
	}
}
