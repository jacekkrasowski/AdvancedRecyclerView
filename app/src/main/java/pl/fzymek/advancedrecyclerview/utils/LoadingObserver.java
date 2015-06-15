package pl.fzymek.advancedrecyclerview.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.MenuItem;
import android.widget.ImageView;

import pl.fzymek.advancedrecyclerview.R;

/**
 * Created by Filip Zymek on 2015-06-15.
 */
public class LoadingObserver {

	MenuItem menuItem;
	boolean isLoading = false;
	private ObjectAnimator animator;

	public LoadingObserver(MenuItem item) {
		this.menuItem = item;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

	public void loadingStarted() {
		isLoading = true;
		menuItem.setVisible(true);
		ImageView view = (ImageView) menuItem.getActionView();
		view.setImageResource(R.mipmap.ic_refresh);
		animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
		animator.setRepeatCount(ValueAnimator.INFINITE);
		animator.setDuration(750);
		animator.start();

	}

	public void loadingStopped() {
		isLoading = false;
		menuItem.setVisible(false);
		if (animator != null) animator.setRepeatCount(0);

	}

}
