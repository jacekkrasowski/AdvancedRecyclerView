package pl.fzymek.advancedrecyclerview.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class AuthenticatorService extends Service {

	Authenticator authenticator;

	@Override
	public void onCreate() {
		super.onCreate();
		authenticator = new Authenticator(this);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return authenticator.getIBinder();
	}
}
