package pl.fzymek.advancedrecyclerview.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import pl.fzymek.advancedrecyclerview.authenticator.AuthenticatorService;
import pl.fzymek.advancedrecyclerview.config.Config;
import pl.fzymek.advancedrecyclerview.provider.Contract;

/**
 * Created by Filip Zymek on 2015-06-23.
 */
public class SyncUtils {
	private final static String TAG = SyncUtils.class.getSimpleName();

	public static void createSyncAccount(Context context) {

		boolean isNewAccount = false;
		boolean setupComplete = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Config.KEY_PREF_ACCOUNT_SETUP_COMPLETE, false);

		Account syncAccount = AuthenticatorService.getSyncAccount();
		AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
		if (accountManager.addAccountExplicitly(syncAccount, null, null)) {
			ContentResolver.setIsSyncable(syncAccount, Contract.AUTHORITY, 1);
//			ContentResolver.setSyncAutomatically(syncAccount, Contract.AUTHORITY, true);
			Bundle extras = new Bundle();
			extras.putBoolean(Config.IS_AUTOMATIC_SYNC, true);
			ContentResolver.addPeriodicSync(syncAccount, Contract.AUTHORITY, extras, TimeUnit.SECONDS.toSeconds(60));
			isNewAccount = true;
		}

		if (isNewAccount || setupComplete) {
			Log.d(TAG, "triggering sync after new account");
			sync();
			PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putBoolean(Config.KEY_PREF_ACCOUNT_SETUP_COMPLETE, true)
				.commit();
		}
	}

	public static void sync() {
		Log.d(TAG, "syncing now...");
		Bundle b = new Bundle();
		// Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
		b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		b.putBoolean(Config.IS_AUTOMATIC_SYNC, false);
		ContentResolver.requestSync(
			AuthenticatorService.getSyncAccount(),            // Sync account
			Contract.AUTHORITY,                                        // Content authority
			b);                                                // Extras
	}
}
