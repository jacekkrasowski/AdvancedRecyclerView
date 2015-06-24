package pl.fzymek.advancedrecyclerview.controller;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import pl.fzymek.advancedrecyclerview.ui.MainUI;

/**
 * Created by Filip Zymek on 2015-06-24.
 */
public class SyncMainController extends MainController implements LoaderManager.LoaderCallbacks<Cursor> {

	protected SyncMainController(Activity activity) {
		super(activity);
	}

	@Override
	public void initialize(MainUI mainUI) {
		super.initialize(mainUI);
	}

	@Override
	public void loadData() {

	}

	@Override
	public void refreshData() {

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}
}
