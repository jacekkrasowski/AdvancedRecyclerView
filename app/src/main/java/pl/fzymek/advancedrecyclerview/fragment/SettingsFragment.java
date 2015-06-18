package pl.fzymek.advancedrecyclerview.fragment;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.config.Config;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	public SettingsFragment() {
		// Required empty public constructor
	}

	public static Fragment newInstance() {
		SettingsFragment fragment = new SettingsFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		updateSummary(Config.KEY_PREF_FAV_ANIMAL);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				getActivity().onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupActionBar() {
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}


	private void updateSummary(String key) {
		Preference preference = findPreference(key);
		SharedPreferences sp = preference.getSharedPreferences();
		switch (preference.getKey()) {
			case Config.KEY_PREF_FAV_ANIMAL:
				Preference pref = findPreference(key);
				String[] animals = getResources().getStringArray(R.array.animals_array);
				Integer val = Integer.parseInt(sp.getString(key, "1"));
				pref.setSummary(getString(R.string.your_favourite_animal) + animals[val - 1]);
				break;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		setupActionBar();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		updateSummary(key);
	}
}
