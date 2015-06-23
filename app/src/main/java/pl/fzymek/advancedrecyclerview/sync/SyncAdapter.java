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
import rx.Subscriber;

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

		Log.d(TAG, "Performing sync with params: "+ searchPhrase + " " + sortOrder);

		if (extras.containsKey(Config.IS_AUTOMATIC_SYNC)) {
			Log.d(TAG, "Performing automatic sync? " + extras.getBoolean(Config.IS_AUTOMATIC_SYNC));
		}

		Observable<Result> images = fiveHundredApi.getImages(searchPhrase, sortOrder);

		images.subscribe(new Subscriber<Result>() {
			@Override
			public void onCompleted() {
				Log.d(TAG, "onCompleted");
			}

			@Override
			public void onError(Throwable e) {
				Log.d(TAG, "onError", e);
			}

			@Override
			public void onNext(Result result) {
				//save to database

				Log.d(TAG, "onNext: " + result);
				Map<String, Image> images = toMap(result.getImages());

				ArrayList<ContentProviderOperation> batch = new ArrayList<>();
				Cursor imagesCursor = resolver.query(Contract.Images.CONTENT_URI, Contract.Images.TABLE_COLUMNS, null, null, null);
				Log.d(TAG, "Found :" + imagesCursor.getCount() + " local entries. Merginig....");

				while (imagesCursor.moveToNext()) {
					syncResult.stats.numEntries++;
					Image localImage = Image.fromCursor(imagesCursor);
					Image networkImage = images.get(localImage.getId());
					if (networkImage != null) {
						//we have hit
						images.remove(localImage.getId());
						Uri existingUri = Contract.Images.CONTENT_URI.buildUpon().appendPath(Integer.toString(localImage.get_id())).build();
						if (needsUpdate(localImage, networkImage)) {
							Log.d(TAG, "image entry needs update");
							batch.add(ContentProviderOperation.newUpdate(existingUri)
									.withValues(networkImage.toContentValues())
									.build()
							);
							syncResult.stats.numUpdates++;
						} else {
							Log.d(TAG, "No action for: " + existingUri);
						}
						updateDisplaySizes(networkImage, batch, syncResult);

					} else {
						Uri deleteUri = Contract.Images.CONTENT_URI.buildUpon().appendPath(Integer.toString(localImage.get_id())).build();
						Log.d(TAG, "image entry needs delete: "+ deleteUri);
						batch.add(ContentProviderOperation.newDelete(deleteUri).build());
						syncResult.stats.numDeletes++;
					}
				}
				imagesCursor.close();


				for (Image image : images.values()) {
					ContentValues values1 = image.toContentValues();
					Log.d(TAG, "image needs insert with values: " + values1);
					batch.add(ContentProviderOperation.newInsert(Contract.Images.CONTENT_URI)
						.withValues(values1)
						.build());
					for (DisplaySize displaySize : image.getDisplaySizes()) {
						ContentValues values = displaySize.toContentValues();
						values.put(Contract.DisplaySizes.IMAGE_ID, image.getId());
						Log.d(TAG, "displaySize needs insert with values: "+ values);
						batch.add(ContentProviderOperation.newInsert(Contract.DisplaySizes.CONTENT_URI)
							.withValues(values)
							.build());
						syncResult.stats.numInserts++;
					}
					syncResult.stats.numInserts++;
				}

				try {
					Log.d(TAG, "Merging ready... applying batch with size (" + batch.size() + ")");
					resolver.applyBatch(Contract.AUTHORITY, batch);
					resolver.notifyChange(Contract.Images.CONTENT_URI, null, false);
					resolver.notifyChange(Contract.DisplaySizes.CONTENT_URI, null, false);
				} catch (RemoteException | OperationApplicationException e) {
					Log.e(TAG, "error applying batch!!!", e);
				}
			}
		});

	}

	private void updateDisplaySizes(Image image, List<ContentProviderOperation> batch, SyncResult syncResult) {
		Cursor displaySizesCursor = resolver.query(
			Contract.DisplaySizes.CONTENT_URI,
			Contract.DisplaySizes.TABLE_COLUMNS,
			Contract.DisplaySizes.IMAGE_ID + " = ?",
			new String[]{image.getId()},
			null
		);

		HashMap<String, DisplaySize> displaySizes = new HashMap<>();
		for(DisplaySize size: image.getDisplaySizes()) {
			displaySizes.put(size.getName(), size);
		}


		while (displaySizesCursor.moveToNext()) {
			syncResult.stats.numEntries++;
			DisplaySize local = DisplaySize.fromCursor(displaySizesCursor);
			DisplaySize remote = displaySizes.get(local.getName());
			if (remote != null) {
				displaySizes.remove(local.getName());
				Uri existingUri = Contract.DisplaySizes.CONTENT_URI.buildUpon().appendPath(Integer.toString(local.get_id())).build();

				if (needsUpdate(local, remote)) {
					ContentValues values = remote.toContentValues();
					Log.d(TAG, "display size needs update with values: " + values);
					batch.add(ContentProviderOperation.newUpdate(Contract.DisplaySizes.CONTENT_URI)
						.withValues(values)
						.build());
					syncResult.stats.numUpdates++;
				} else {
					Log.d(TAG, "No action for: " + existingUri);
				}

			} else {
				Uri deleteUri = Contract.DisplaySizes.CONTENT_URI.buildUpon().appendPath(Integer.toString(local.get_id())).build();
				Log.d(TAG, "display size needs delete: "+ deleteUri);
				batch.add(ContentProviderOperation.newDelete(deleteUri).build());
				syncResult.stats.numDeletes++;
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
		int key = Integer.parseInt(sharedPreferences.getString(Config.KEY_PREF_FAV_ANIMAL, Config.PREF_FAV_ANIMAL_DEFAULT));
		String animal = getContext().getResources().getStringArray(R.array.animals_array)[key - 1];
		return animal;
	}

	protected String getSortOrder() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		String[] orderArray = getContext().getResources().getStringArray(R.array.sort_order_values);
		int pos = Integer.parseInt(sharedPreferences.getString(Config.KET_PREF_SORT_ORDER, getContext().getString(R.string.sort_order_pref_default_value)));
		return orderArray[pos -1];
	}
}
