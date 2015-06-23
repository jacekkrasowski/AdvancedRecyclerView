package pl.fzymek.advancedrecyclerview.config;

import java.util.concurrent.TimeUnit;

/**
 * Created by Filip Zymek on 2015-06-09.
 */
public interface Config {

	int DB_VERSION = 1;
	String DB_NAME = "images.db";

	String KEY_PREF_FAV_ANIMAL = "fav_animal";
	String KET_PREF_SORT_ORDER = "sort_order";
	String KEY_PREF_USE_STAGGERED_GRID = "use_staggered_grid";

	String KEY_PREF_ACCOUNT_SETUP_COMPLETE = "key_pref_account_setup_complete";

	String PREF_FAV_ANIMAL_DEFAULT = "1";

	String IS_AUTOMATIC_SYNC = "is_automatic_sync";
	String ACCOUNT_NAME = "sync";

	String ACCOUNT_TYPE = "pl.fzymek.advancedrecyclerview";
	long DEFAULT_VALIDITY = TimeUnit.SECONDS.toMillis(5);
}
