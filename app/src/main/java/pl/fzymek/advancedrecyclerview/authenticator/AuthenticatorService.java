package pl.fzymek.advancedrecyclerview.authenticator;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import pl.fzymek.advancedrecyclerview.config.Config;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class AuthenticatorService extends Service {

	private final static String TAG = AuthenticatorService.class.getSimpleName();

	Authenticator authenticator;

	public static Account getSyncAccount() {
		return new Account(Config.ACCOUNT_NAME, Config.ACCOUNT_TYPE);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "creating AuthenticatorService");
		authenticator = new Authenticator(this);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return authenticator.getIBinder();
	}
}
