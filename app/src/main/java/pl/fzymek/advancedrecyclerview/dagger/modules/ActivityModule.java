package pl.fzymek.advancedrecyclerview.dagger.modules;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Filip Zymek on 2015-06-19.
 */
@Module
public class ActivityModule {

	private final Activity activity;

	public ActivityModule(Activity activity) {
		this.activity = activity;

	}

	@Provides
	public Activity getActivity() {
		return this.activity;
	}

}
