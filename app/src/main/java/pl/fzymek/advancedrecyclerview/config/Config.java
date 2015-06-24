package pl.fzymek.advancedrecyclerview.config;

import java.util.concurrent.TimeUnit;

/**
 * Created by Filip Zymek on 2015-06-09.
 */
public interface Config {

	int DB_VERSION = 1;
	String DB_NAME = "images.db";

	String KEY_PREF_FAV_ANIMAL = "fav_animal";
	String KEY_PREF_SORT_ORDER = "sort_order";
	String KEY_PREF_PURGE_BY_VALIDITY = "purge_by_validity";
	String KEY_PREF_USE_STAGGERED_GRID = "use_staggered_grid";
	String KEY_PREF_ACCOUNT_SETUP_COMPLETE = "key_pref_account_setup_complete";

	String EXTRA_IS_AUTOMATIC_SYNC = "is_automatic_sync";

	String ACCOUNT_NAME = "sync";
	String ACCOUNT_TYPE = "pl.fzymek.advancedrecyclerview";

	long DEFAULT_VALIDITY = TimeUnit.MINUTES.toMillis(2);
}
