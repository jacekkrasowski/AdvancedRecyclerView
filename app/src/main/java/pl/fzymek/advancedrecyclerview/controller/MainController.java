package pl.fzymek.advancedrecyclerview.controller;

import android.app.Activity;

import pl.fzymek.advancedrecyclerview.ui.MainUI;

/**
 * Created by Filip Zymek on 2015-06-08.
 */
public class MainController extends ActivityController<MainUI> {

	protected MainUI ui;

	public MainController(Activity activity) {
		super(activity);
	}

	@Override
	public void initialize(MainUI mainUI) {
		this.ui = mainUI;
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
