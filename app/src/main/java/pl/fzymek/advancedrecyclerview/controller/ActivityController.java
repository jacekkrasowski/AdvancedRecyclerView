package pl.fzymek.advancedrecyclerview.controller;

import android.app.Activity;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Filip Zymek on 2014-11-05.
 */
public abstract class ActivityController<UI> extends CompositeSubscriptionController<UI> {

	protected Activity activity;

	protected ActivityController(Activity activity) {
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
}
