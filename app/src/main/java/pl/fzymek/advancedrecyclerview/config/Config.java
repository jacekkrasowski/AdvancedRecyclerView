package pl.fzymek.advancedrecyclerview.config;

import java.util.concurrent.TimeUnit;

/**
 * Created by Filip Zymek on 2015-06-09.
 */
public interface Config {

	int DB_VERSION = 1;
	String DB_NAME = "images.db";

	String KEY_PREF_FAV_ANIMAL = "fav_animal";
	String PREF_FAV_ANIMAL_DEFAULT = "1";
	String KEY_PREF_USE_STAGGERED_GRID = "use_staggered_grid";
	long DEFAULT_VALIDITY = TimeUnit.MINUTES.toMillis(2);
	String IS_AUTOMATIC_SYNC = "is_automatic_sync";
	String KEY_PREF_ACCOUNT_SETUP_COMPLETE = "key_pref_account_setup_complete";

	String ACCOUNT_TYPE = "pl.fzymek.advancedrecyclerview";
}
