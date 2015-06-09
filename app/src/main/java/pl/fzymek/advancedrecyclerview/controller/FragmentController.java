package pl.fzymek.advancedrecyclerview.controller;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by Filip Zymek on 2014-11-05.
 */
public abstract class FragmentController<UI> extends CompositeSubscriptionController<UI> {

	protected Fragment fragment;

	public FragmentController(Fragment fragment) {
		this.fragment = fragment;
	}

	public Fragment getFragment() {
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}

	public void runOnUiThread(Runnable action) {
		if (fragment != null) {
			Activity activity = fragment.getActivity();
			if (activity != null) {
				activity.runOnUiThread(action);
			}
		}
	}
}
