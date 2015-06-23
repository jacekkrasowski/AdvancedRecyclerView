package pl.fzymek.advancedrecyclerview.authenticator;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class AuthenticatorService extends Service {

	private final static String TAG = AuthenticatorService.class.getSimpleName();

	Authenticator authenticator;

	private static final String ACCOUNT_TYPE = "pl.fzymek.advancedrecyclerview";
	public static final String ACCOUNT_NAME = "sync";

	public static Account getSyncAccount() {
		return new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
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
