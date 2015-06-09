package pl.fzymek.advancedrecyclerview.ui;

public interface UILifecycleObserver {

	void onStart();

	void onStop();

	void onPause();

	void onResume();

	void onDestroy();

}