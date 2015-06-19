package pl.fzymek.advancedrecyclerview.dagger.modules;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.fzymek.advancedrecyclerview.application.AdvancedRecyclerViewApplication;

/**
 * Created by Filip Zymek on 2015-06-19.
 */
@Module
public class ApplicationModule {

	private final AdvancedRecyclerViewApplication application;
	private final ImageLoaderConfiguration configuration;
	private final DisplayImageOptions options;

	public ApplicationModule(AdvancedRecyclerViewApplication application) {
		this.application = application;
		configuration = new ImageLoaderConfiguration.Builder(application)
			.memoryCache(new LruMemoryCache(getCacheMemorySize(application)))
			.build();

		options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.resetViewBeforeLoading(true)
			.showImageOnLoading(new ColorDrawable(application.getResources().getColor(android.R.color.darker_gray)))
			.build();

		ImageLoader.getInstance().init(configuration);

	}

	@Provides
	@Singleton
	public Context getApplicationContext() {
		return this.application;
	}

	@Provides
	@Singleton
	public ImageLoaderConfiguration getImageLoaderConfiguration() {
		return configuration;
	}

	@Provides
	@Singleton
	public DisplayImageOptions getDisplayImageOptions() {
		return options;
	}

	private int getCacheMemorySize(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getMemoryClass() << 17; //(x * 1024 * 1024) / 8
	}
}
