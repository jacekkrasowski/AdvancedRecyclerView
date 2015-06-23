package pl.fzymek.advancedrecyclerview.activity;

import android.accounts.Account;
import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
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

import javax.inject.Inject;

import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.authenticator.AuthenticatorService;
import pl.fzymek.advancedrecyclerview.fragment.MainFragment;
import pl.fzymek.advancedrecyclerview.fragment.SettingsFragment;
import pl.fzymek.advancedrecyclerview.provider.Contract;
import pl.fzymek.advancedrecyclerview.sync.SyncUtils;


public class MainActivity extends BaseActivity {

	private final static String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupPreferences();

		if (savedInstanceState == null) {
			MainFragment f = MainFragment.newInstance();
			getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, f, "main")
				.commit();
		}

		Log.d(TAG, "Setting automatic syncs");
		SyncUtils.createSyncAccount(this);
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

}
