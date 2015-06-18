package pl.fzymek.advancedrecyclerview.activity;

import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.fragment.MainFragment;
import pl.fzymek.advancedrecyclerview.fragment.SettingsFragment;


public class MainActivity extends AppCompatActivity {

	ImageLoaderConfiguration config;
	DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupPreferences();
		setupImageLoader();

		if (savedInstanceState == null) {
			MainFragment f = MainFragment.newInstance();
			getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, f, "main")
				.commit();
		}

	}

	@Override
	public void onBackPressed() {
		if (getFragmentManager().getBackStackEntryCount() > 0) {
			getFragmentManager().popBackStack();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
			case android.R.id.home:
				onBackPressed();
				return true;

		}
		return super.onOptionsItemSelected(item);
	}


	private void setupPreferences() {
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
	}

	private void setupImageLoader() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		int memCacheSize = am.getMemoryClass() << 17; // (x *1024 *1024)/8
		Log.d("MainActivity", "Image cache will have " + memCacheSize + " bytes (" + (memCacheSize >> 20) + " MB)");
		config = new ImageLoaderConfiguration.Builder(this)
			.memoryCache(new LruMemoryCache(memCacheSize))
			.build();
		ImageLoader.getInstance().init(config);
		options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.resetViewBeforeLoading(true)
			.showImageOnLoading(new ColorDrawable(getResources().getColor(android.R.color.darker_gray)))
			.build();
	}

	public DisplayImageOptions getDisplayImageOptions() {
		return options;
	}
}
