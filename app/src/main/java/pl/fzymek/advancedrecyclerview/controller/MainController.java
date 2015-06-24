package pl.fzymek.advancedrecyclerview.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.config.API;
import pl.fzymek.advancedrecyclerview.config.Config;
import pl.fzymek.advancedrecyclerview.network.FiveHundredPxAPI;
import pl.fzymek.advancedrecyclerview.ui.MainUI;
import retrofit.RestAdapter;

/**
 * Created by Filip Zymek on 2015-06-24.
 */
public abstract class MainController extends ActivityController<MainUI> {
	protected MainUI ui;

	protected MainController(Activity activity) {
		super(activity);
	}

	abstract public void loadData();
	abstract public void refreshData();

	@Override
	public void initialize(MainUI mainUI) {
		this.ui = mainUI;
	}

	protected String getSearchPhrase() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int key = Integer.parseInt(sharedPreferences.getString(Config.KEY_PREF_FAV_ANIMAL, getActivity().getString(R.string.fav_animal_pref_default_value)));
		String animal = getActivity().getResources().getStringArray(R.array.animals_array)[key - 1];
		return animal;
	}


	@Override
	public void saveState(Object outState) {

	}

	@Override
	public void restoreState(Object savedState) {

	}

	@Override
	public void onStart() {
	}

	@Override
	public void onStop() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void onResume() {

	}
}
