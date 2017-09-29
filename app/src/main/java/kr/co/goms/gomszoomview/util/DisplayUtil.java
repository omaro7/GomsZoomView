package kr.co.goms.gomszoomview.util;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DisplayUtil {

	public static String TAG = DisplayUtil.class.getSimpleName();
	public static Activity mActivity;

	public DisplayUtil(final Activity activity){
		mActivity = activity;
	}

	public static String getDeviceDisplaySize(Activity activity){
		WindowManager w = activity.getWindowManager();
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
		// since SDK_INT = 1;
		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
			try {
				widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
				heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
			} catch (Exception ignored) {
			}
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 17)
			try {
				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
				widthPixels = realSize.x;
				heightPixels = realSize.y;
			} catch (Exception ignored) {
		}

		String size = widthPixels + ":" + heightPixels;
		return size;
	}

	/** 3: 4 = numerator(분자) : denominator(분모) */
	public static int getRadioHeight(int targetWidth, int numerator, int denominator){
		int resultHeight = (targetWidth*denominator)/numerator;
		return resultHeight;
	}

	/** 3: 4 = numerator(분자) : denominator(분모) */
	public static int getRadioWidth(int targetHeight, int numerator, int denominator){
		int resultWidth = (targetHeight*numerator)/denominator;
		return resultWidth;
	}

	/**
	 *  (original height / original width) x new width = new height
	 * @param targetWidth
	 * @param originalWidth
	 * @param originalHeight
	 * @return
	 */
	public static int getRadioHeightFromOrginalSize(int targetWidth, int originalWidth, int originalHeight){
		GomsLog.d(TAG, "getRadioHeightFromOrginalSize()");
		int resultHeight = (int)(((float)originalHeight / (float)originalWidth) * (float)targetWidth);
		return resultHeight;
	}

	public static String getFrameLayoutSize(Activity activity, String aspect){
		String deviceSize = getDeviceDisplaySize(activity);
		String[] deviceSizeArray = deviceSize.split(":");

		String[] aspectArray = aspect.split(":");
		int photoLayoutHeight = getRadioHeightFromOrginalSize(StringUtil.stringToInt(deviceSizeArray[0]),  StringUtil.stringToInt(aspectArray[0]), StringUtil.stringToInt(aspectArray[1]));
		String tmp = deviceSizeArray[0] + ":" + StringUtil.intToString(photoLayoutHeight);
		return tmp;
	}

	public static String getPhotoLayoutSize(Activity activity, String filewh){
		String deviceSize = getDeviceDisplaySize(activity);
		String[] deviceSizeArray = deviceSize.split(":");

		String[] filewhArray = filewh.split(":");
		int photoLayoutHeight = getRadioHeightFromOrginalSize(StringUtil.stringToInt(deviceSizeArray[0]),  StringUtil.stringToInt(filewhArray[0]), StringUtil.stringToInt(filewhArray[1]));
		String tmp = deviceSizeArray[0] + ":" + StringUtil.intToString(photoLayoutHeight);
		return tmp;
	}

	/**
	 * 비율 구하기
	 * @param width
	 * @param height
	 * @return
	 */
	public static String getAspectRatioFromWH(int width, int height) {
		int gcf = greatestCommonFactor(width, height);
		if( gcf > 0 ) {
			// had a Google Play crash due to gcf being 0!? Implies width must be zero
			width /= gcf;
			height /= gcf;
		}
		String apsect = width + ":" + height;
		return apsect;
	}

	private static int greatestCommonFactor(int a, int b) {
		while( b > 0 ) {
			int temp = b;
			b = a % b;
			a = temp;
		}
		return a;
	}

	/**
	 *
	 * @param aspectRatio
	 * @param type 0 : first 1: second
	 * @return
	 */
	public static String getAspectRatioSplitValue(String aspectRatio, int type) {
		String[] aspect = aspectRatio.split(":");
		return aspect[type];
	}

}
