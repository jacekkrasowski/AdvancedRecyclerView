package pl.fzymek.advancedrecyclerview.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.config.API;
import pl.fzymek.advancedrecyclerview.config.Config;
import pl.fzymek.advancedrecyclerview.model.DisplaySize;
import pl.fzymek.advancedrecyclerview.model.Image;
import pl.fzymek.advancedrecyclerview.model.Result;
import pl.fzymek.advancedrecyclerview.network.FiveHundredPxAPI;
import pl.fzymek.advancedrecyclerview.provider.Contract;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private final static String TAG = SyncAdapter.class.getSimpleName();

	private final RestAdapter restAdapter;
	private final FiveHundredPxAPI fiveHundredApi;
	ContentResolver resolver;


	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		Log.d(TAG, "creating sync adapter");
		resolver = context.getContentResolver();
		restAdapter = new RestAdapter.Builder()
			.setEndpoint(API.FIVE_HUNDRED_API_ENDPOINT)
			.build();
		fiveHundredApi = restAdapter.create(FiveHundredPxAPI.class);
	}

	public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		Log.d(TAG, "creating sync adapter (parallel)");
		resolver = context.getContentResolver();
		restAdapter = new RestAdapter.Builder()
			.setEndpoint(API.FIVE_HUNDRED_API_ENDPOINT)
			.build();
		fiveHundredApi = restAdapter.create(FiveHundredPxAPI.class);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
		String searchPhrase = getSearchPhrase();
		String sortOrder = getSortOrder();

		Log.d(TAG, "Performing sync with params: " + searchPhrase + " " + sortOrder);

		if (extras.containsKey(Config.EXTRA_IS_AUTOMATIC_SYNC)) {
			Log.d(TAG, "Performing automatic sync? " + extras.getBoolean(Config.EXTRA_IS_AUTOMATIC_SYNC));
		}

		Observable<Result> images = fiveHundredApi.getImages(searchPhrase, sortOrder);

		images.subscribe(
			result -> processResult(result, syncResult),
			error -> Log.d(TAG, "onError", error),
			() -> Log.d(TAG, "onCompleted")
		);
	}

	public void processResult(Result result, SyncResult syncResult) {
		//merge results to database
		Map<String, Image> images = toMap(result.getImages());
		ArrayList<ContentProviderOperation> batch = new ArrayList<>();

		mergeExistingImagesInDb(images, batch, syncResult);
		addNewImagesToDb(images, batch, syncResult);

		try {
			Log.d(TAG, "Merge batch ready... applying (" + batch.size() + ") changes");
			resolver.applyBatch(Contract.AUTHORITY, batch);
			resolver.notifyChange(Contract.Images.CONTENT_URI, null, false);
			resolver.notifyChange(Contract.DisplaySizes.CONTENT_URI, null, false);
		} catch (RemoteException | OperationApplicationException e) {
			Log.e(TAG, "error applying batch!!!", e);
		}

		Log.d(TAG, "Syncing result: "+ syncResult);
	}

	private void mergeExistingImagesInDb(Map<String, Image> images, ArrayList<ContentProviderOperation> batch, SyncResult syncResult) {
		//get local images
		Cursor imagesCursor = resolver.query(
			Contract.Images.CONTENT_URI,
			Contract.Images.TABLE_COLUMNS,
			null,
			null,
			null);

		while (imagesCursor.moveToNext()) {
			syncResult.stats.numEntries++;
			Image local = Image.fromCursor(imagesCursor);
			long now = Calendar.getInstance().getTimeInMillis();
			Log.d(TAG, "local.validity = " + local.getValidity() + " now: " + now);
			Image remote = images.get(local.getId());
			if (remote != null) {
				//we have hit - remove entry to prevent addition and check if local data needs update
				images.remove(local.getId());

				Uri existingUri = Contract.Images.CONTENT_URI.buildUpon().appendPath(Integer.toString(local.get_id())).build();

				//check if we need to update local data
				if (needsUpdate(local, remote)) {
					batch.add(ContentProviderOperation.newUpdate(existingUri)
							.withValues(remote.toContentValues())
							.build()
					);
					syncResult.stats.numUpdates++;
				} else {
					Log.d(TAG, "No action for: " + existingUri);
				}

				//update display sizes for this image entry
				mergeExistingDisplaySizesToDb(remote, batch, syncResult);

			} else {
				if (updateByNetwork()) {
					//network results does not contain cached result, delete it
					Uri deleteUri = Contract.Images.CONTENT_URI.buildUpon().appendPath(Integer.toString(local.get_id())).build();
					batch.add(ContentProviderOperation.newDelete(deleteUri).build());
					syncResult.stats.numDeletes++;
				}
			}
		}
		imagesCursor.close();
	}

	private boolean updateByNetwork() {
		return !PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(Config.KEY_PREF_PURGE_BY_VALIDITY, getContext().getResources().getBoolean(R.bool.purge_by_validity_default_value));
	}

	private void addNewImagesToDb(Map<String, Image> images, ArrayList<ContentProviderOperation> batch, SyncResult syncResult) {
		for (Image image : images.values()) {
			//add new image entry to db
			ContentValues imgValues = image.toContentValues();
			batch.add(ContentProviderOperation.newInsert(Contract.Images.CONTENT_URI)
				.withValues(imgValues)
				.build());
			//add new displaysizes entries for new image
			for (DisplaySize displaySize : image.getDisplaySizes()) {
				ContentValues dsValues = displaySize.toContentValues();
				dsValues.put(Contract.DisplaySizes.IMAGE_ID, image.getId());
				batch.add(ContentProviderOperation.newInsert(Contract.DisplaySizes.CONTENT_URI)
					.withValues(dsValues)
					.build());
				syncResult.stats.numInserts++;
			}
			syncResult.stats.numInserts++;
		}
	}

	private void mergeExistingDisplaySizesToDb(Image image, List<ContentProviderOperation> batch, SyncResult syncResult) {
		//get local ds entries
		Cursor displaySizesCursor = resolver.query(
			Contract.DisplaySizes.CONTENT_URI,
			Contract.DisplaySizes.TABLE_COLUMNS,
			Contract.DisplaySizes.IMAGE_ID + " = ?",
			new String[]{image.getId()},
			null
		);

		HashMap<String, DisplaySize> displaySizes = new HashMap<>();
		for (DisplaySize size : image.getDisplaySizes()) {
			displaySizes.put(size.getName(), size);
		}

		while (displaySizesCursor.moveToNext()) {
			syncResult.stats.numEntries++;
			DisplaySize local = DisplaySize.fromCursor(displaySizesCursor);
			DisplaySize remote = displaySizes.get(local.getName());

			if (remote != null) {
				//remove entry fom map to prevent duplicates and check if we need update
				displaySizes.remove(local.getName());
				Uri existingUri = Contract.DisplaySizes.CONTENT_URI.buildUpon().appendPath(Integer.toString(local.get_id())).build();

				//check if we need to update local data
				if (needsUpdate(local, remote)) {
					ContentValues values = remote.toContentValues();
					batch.add(ContentProviderOperation.newUpdate(Contract.DisplaySizes.CONTENT_URI)
						.withValues(values)
						.build());
					syncResult.stats.numUpdates++;
				} else {
					Log.d(TAG, "No action for: " + existingUri);
				}

			} else {
				if (updateByNetwork()) {
					//network results does not contain cached result, delete it
					Uri deleteUri = Contract.DisplaySizes.CONTENT_URI.buildUpon().appendPath(Integer.toString(local.get_id())).build();
					batch.add(ContentProviderOperation.newDelete(deleteUri).build());
					syncResult.stats.numDeletes++;
				}
			}

		}
		displaySizesCursor.close();
	}

	private <T> boolean needsUpdate(T local, T network) {
		return !local.equals(network);
	}

	private HashMap<String, Image> toMap(List<Image> images) {
		HashMap<String, Image> map = new HashMap<>();
		for (Image image : images) {
			map.put(image.getId(), image);
		}
		return map;
	}

	protected String getSearchPhrase() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		int key = Integer.parseInt(sharedPreferences.getString(Config.KEY_PREF_FAV_ANIMAL, getContext().getString(R.string.fav_animal_pref_default_value)));
		String animal = getContext().getResources().getStringArray(R.array.animals_array)[key - 1];
		return animal;
	}

	protected String getSortOrder() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		String[] orderArray = getContext().getResources().getStringArray(R.array.sort_order_values);
		int pos = Integer.parseInt(sharedPreferences.getString(Config.KEY_PREF_SORT_ORDER, getContext().getString(R.string.sort_order_pref_default_value)));
		return orderArray[pos - 1];
	}
}
