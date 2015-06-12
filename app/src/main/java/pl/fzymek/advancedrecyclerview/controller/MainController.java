package pl.fzymek.advancedrecyclerview.controller;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.config.API;
import pl.fzymek.advancedrecyclerview.config.Config;
import pl.fzymek.advancedrecyclerview.model.Result;
import pl.fzymek.advancedrecyclerview.network.FiveHundredPxAPI;
import pl.fzymek.advancedrecyclerview.ui.MainUI;
import pl.fzymek.advancedrecyclerview.utils.SimpleCache;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Filip Zymek on 2015-06-08.
 */
public class MainController extends ActivityController<MainUI> implements Observer<Result> {

	protected MainUI ui;
	protected RestAdapter restAdapter;
	FiveHundredPxAPI fiveHundredApi;

	public MainController(Activity activity) {
		super(activity);
	}

	@Override
	public void initialize(MainUI mainUI) {
		this.ui = mainUI;
		restAdapter = new RestAdapter.Builder()
			.setEndpoint(API.FIVE_HUNDRED_API_ENDPOINT)
			.build();
		fiveHundredApi = restAdapter.create(FiveHundredPxAPI.class);
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

	public void loadData() {
		ui.onLoadingStarted();
		String searchPhrase = getSearchPhrase();
		Observable<Result> imagesObservable = buildImagesObservable(searchPhrase);
		subscribeWith(imagesObservable
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.filter(result -> true)
				.cache()
				.subscribe(this)
		);
	}

	@Override
	public void onCompleted() {
		Log.d("MainController", "onCompleted");
		ui.onLoadingStopped();
	}

	@Override
	public void onError(Throwable e) {
		Log.d("MainController", "onError");
		ui.onError(e);
	}

	@Override
	public void onNext(Result result) {
		Log.d("MainController", "onNext");
		ui.onDisplayImages(result.getImages());
	}

	protected String getSearchPhrase() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int key = Integer.parseInt(sharedPreferences.getString(Config.KEY_PREF_FAV_ANIMAL, Config.PREF_FAV_ANIMAL_DEFAULT));
		String animal = getActivity().getResources().getStringArray(R.array.animals_array)[key - 1];
		return animal;
	}

	private Observable<Result> buildImagesObservable(String searchPhrase) {
		Observable<Result> imagesObservable = null;
		if (hasCache()) {
			Log.d("MainController", "hasCache, fetching cached object");
			imagesObservable = (Observable<Result>) getCache().get(searchPhrase);
		}

		if (imagesObservable == null) {
			Log.d("MainController", "Cached object is null");
			imagesObservable = fiveHundredApi.getImages(searchPhrase);
			if (hasCache()) {
				Log.d("MainController", "putting object into cache");
				getCache().put(searchPhrase, imagesObservable);
			}
		}
		return imagesObservable;
	}
}
