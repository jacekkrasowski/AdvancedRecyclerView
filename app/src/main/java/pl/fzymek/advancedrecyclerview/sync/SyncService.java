package pl.fzymek.advancedrecyclerview.sync;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class SyncService extends Service {

	private final static String TAG = SyncService.class.getSimpleName();

	private static SyncAdapter syncAdapter;

	private static final Object lock = new Object();

	@Override
	public void onCreate() {
		Log.d(TAG, "creating sync service");
		synchronized (lock) {
			if (syncAdapter == null) {
				syncAdapter = new SyncAdapter(getApplicationContext(), true);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "getting binder to adapter");
		return syncAdapter.getSyncAdapterBinder();
	}

}
