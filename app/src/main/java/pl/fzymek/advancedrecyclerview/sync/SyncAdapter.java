package pl.fzymek.advancedrecyclerview.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;

import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.config.API;
import pl.fzymek.advancedrecyclerview.config.Config;
import pl.fzymek.advancedrecyclerview.model.Result;
import pl.fzymek.advancedrecyclerview.network.FiveHundredPxAPI;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private final RestAdapter restAdapter;
	private final FiveHundredPxAPI fiveHundredApi;
	ContentResolver resolver;


	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		resolver = context.getContentResolver();
		restAdapter = new RestAdapter.Builder()
			.setEndpoint(API.FIVE_HUNDRED_API_ENDPOINT)
			.build();
		fiveHundredApi = restAdapter.create(FiveHundredPxAPI.class);
	}

	public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		resolver = context.getContentResolver();
		restAdapter = new RestAdapter.Builder()
			.setEndpoint(API.FIVE_HUNDRED_API_ENDPOINT)
			.build();
		fiveHundredApi = restAdapter.create(FiveHundredPxAPI.class);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
		String searchPhrase = getSearchPhrase();
		Observable<Result> images = fiveHundredApi.getImages(searchPhrase);

		images.subscribe(new Subscriber<Result>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {

			}

			@Override
			public void onNext(Result result) {
				//save to database
			}
		});

	}

	protected String getSearchPhrase() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		int key = Integer.parseInt(sharedPreferences.getString(Config.KEY_PREF_FAV_ANIMAL, Config.PREF_FAV_ANIMAL_DEFAULT));
		String animal = getContext().getResources().getStringArray(R.array.animals_array)[key - 1];
		return animal;
	}
}
