package pl.fzymek.advancedrecyclerview.controller;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import pl.fzymek.advancedrecyclerview.model.DisplaySize;
import pl.fzymek.advancedrecyclerview.model.Image;
import pl.fzymek.advancedrecyclerview.provider.Contract;
import pl.fzymek.advancedrecyclerview.sync.SyncUtils;
import pl.fzymek.advancedrecyclerview.ui.MainUI;

/**
 * Created by Filip Zymek on 2015-06-24.
 */
public class SyncMainController extends MainController implements LoaderManager.LoaderCallbacks<Cursor> {

	private final static String TAG = SyncMainController.class.getSimpleName();

	private static final int IMAGE_CURSOR_ID = 1;
	private static final int DS_CURSOR_ID = 2;
	private Map<String, Image> images;

	public SyncMainController(Activity activity) {
		super(activity);
	}

	@Override
	public void initialize(MainUI mainUI) {
		super.initialize(mainUI);
	}

	@Override
	public void loadData() {
		Log.d(TAG, "starting loader for images");
		getActivity().getLoaderManager().initLoader(IMAGE_CURSOR_ID, null, this).forceLoad();
	}

	@Override
	public void refreshData() {
		Log.d(TAG, "starting loader for images");
		images = null;
		SyncUtils.sync();
//		getActivity().getLoaderManager().restartLoader(IMAGE_CURSOR_ID, null, this).forceLoad();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		switch (id) {
			case IMAGE_CURSOR_ID:
				Log.d(TAG, "creating loader for images");
				ui.onLoadingStarted();
				return new CursorLoader(
					getActivity(),
					Contract.Images.CONTENT_URI,
					Contract.Images.TABLE_COLUMNS,
					null,
					null,
					null
				);
			case DS_CURSOR_ID:
				Log.d(TAG, "creating loader for ds");
				return new CursorLoader(
					getActivity(),
					Contract.DisplaySizes.CONTENT_URI,
					Contract.DisplaySizes.TABLE_COLUMNS,
					null,
					null,
					null
				);
			default:
				return null;
		}
	}

	;

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

		if (data == null) {
			Log.e(TAG, "cursor is null!");
			ui.onError(new Exception("Cannot load data!"));
			return;
		}

		if (data.getCount() <= 0) {
			Log.w(TAG, "Have empty cursor!!! with id: " + loader.getId());
			//restart loader to get contents
			getActivity().getLoaderManager().restartLoader(loader.getId(), null, this).forceLoad();
			if (!data.isClosed()) data.close();
			return;
		}

		switch (loader.getId()) {
			case IMAGE_CURSOR_ID:
				Log.d(TAG, "images loader finshed with: " + data.getCount() + " entries");
				images = createImages(data);
				getActivity().getLoaderManager().initLoader(DS_CURSOR_ID, null, this).forceLoad();
				break;
			case DS_CURSOR_ID:
				Log.d(TAG, "ds loader finshed with: " + data.getCount() + " entries");
				updateImages(data);
				ui.onLoadingStopped();
				ui.onDisplayImages(new ArrayList<>(images.values()));
				break;
		}
		data.close();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
			case IMAGE_CURSOR_ID:
				Log.d(TAG, "reset image loader");
				break;
			case DS_CURSOR_ID:
				Log.d(TAG, "reset ds loader");
				break;
		}
	}

	private Map<String, Image> createImages(Cursor data) {
		Map<String, Image> tmp = new LinkedHashMap<>();
		while (data.moveToNext()) {
//			Log.d(TAG, "cursor img: " + DatabaseUtils.dumpCurrentRowToString(data));
			Image img = Image.fromCursor(data);
//			Log.d(TAG, "Img from cursor: " + img);
			tmp.put(img.getId(), img);
		}
		return tmp;
	}

	private void updateImages(Cursor data) {
		while (data.moveToNext()) {
//			Log.d(TAG, "cursor ds: " + DatabaseUtils.dumpCurrentRowToString(data));
			DisplaySize ds = DisplaySize.fromCursor(data);
			String imgId = ds.getImageId();
			if (images == null) {
				Log.e(TAG, "Something went wrong!!!");
				ui.onError(new Exception("Something went wrong!!"));
				break;
			}
//			Log.d(TAG, "Updating ds for image: "+ imgId + " " + ds);
			Image image = images.get(imgId);
			if (image != null) {
//				Log.d(TAG, "ds from cursor: " + ds);
				image.getDisplaySizes().add(ds);
			}
		}
	}
}
