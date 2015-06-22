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
}
