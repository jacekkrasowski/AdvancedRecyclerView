package pl.fzymek.advancedrecyclerview.controller;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Filip Zymek on 2014-11-27.
 *
 * Convenience class for controller which manage more than one subscription at a time
 *
 */
public abstract class CompositeSubscriptionController<UI> implements Controller<UI> {

	/**
	 * Holder for subscriptions
	 */
	CompositeSubscription compositeSubscription;

	protected CompositeSubscriptionController() {
		compositeSubscription = new CompositeSubscription();
	}

	/**
	 * Add subscription to managed subscription list
	 * @param subscription
	 */
	protected void subscribeWith(Subscription subscription) {
		compositeSubscription.add(subscription);
	}

	/**
	 * Unsubscribes all previously subscribed subscriptions
	 */
	protected void unsubscribeWithAll() {
		compositeSubscription.unsubscribe();
	}

	/**
	 * Will unsubscribe all subscriptions
	 */
	@Override
	public void onDestroy() {
		unsubscribeWithAll();
	}
}
