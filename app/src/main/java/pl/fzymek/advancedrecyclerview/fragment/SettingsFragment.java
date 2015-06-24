package pl.fzymek.advancedrecyclerview.fragment;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.config.Config;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private final static String [] PREFERENCES = {
		Config.KEY_PREF_FAV_ANIMAL,
		Config.KEY_PREF_SORT_ORDER,
		Config.KEY_PREF_PURGE_BY_VALIDITY
	};

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
		for(String pref: PREFERENCES) {
			updateSummary(pref);
		}
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
		Preference pref = findPreference(key);
		switch (preference.getKey()) {
			case Config.KEY_PREF_FAV_ANIMAL:
				String[] animals = getResources().getStringArray(R.array.animals_array);
				int val = Integer.parseInt(sp.getString(key, Integer.toString(getResources().getInteger(R.integer.fav_animal_pref_default_value))));
				pref.setSummary(getString(R.string.your_favourite_animal,animals[val - 1]));
				break;

			case Config.KEY_PREF_SORT_ORDER:
				String[] orderArray = getResources().getStringArray(R.array.sort_order_array);
				int pos = Integer.parseInt(sp.getString(Config.KEY_PREF_SORT_ORDER, getString(R.string.sort_order_pref_default_value)));
				pref.setSummary(getString(R.string.your_sort_order, orderArray[pos -1]));
				break;

			case Config.KEY_PREF_PURGE_BY_VALIDITY:
				boolean purgeByValidity = sp.getBoolean(Config.KEY_PREF_PURGE_BY_VALIDITY, getResources().getBoolean(R.bool.purge_by_validity_default_value));
				if (purgeByValidity) {
					pref.setSummary(getString(R.string.your_update_will_use_validity));
				} else {
					pref.setSummary(getString(R.string.your_update_will_use_network));
				}
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
