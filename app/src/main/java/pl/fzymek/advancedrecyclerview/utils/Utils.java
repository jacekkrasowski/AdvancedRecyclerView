package pl.fzymek.advancedrecyclerview.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import pl.fzymek.advancedrecyclerview.BuildConfig;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class Utils {
	private Utils() {

	}

	public static boolean isLandscape(final Context context) {
		int orientation = context.getResources().getConfiguration().orientation;
		return orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	public static boolean hasApi(final int apiLevel) {
		return Build.VERSION.SDK_INT >= apiLevel;
	}
}
