package pl.fzymek.advancedrecyclerview.sync;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class SyncService extends IntentService {

	private static SyncAdapter syncAdapter;

	private static final Object lock = new Object();

	public SyncService() {
		super("SyncService");
		synchronized (lock) {
			if (syncAdapter == null) {
				syncAdapter = new SyncAdapter(getApplicationContext(), true);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return syncAdapter.getSyncAdapterBinder();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle config = intent.getExtras();
		syncAdapter.onPerformSync(null, config, null, null, null);
	}

}
