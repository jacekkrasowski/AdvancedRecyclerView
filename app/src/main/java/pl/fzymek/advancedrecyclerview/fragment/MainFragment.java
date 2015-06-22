package pl.fzymek.advancedrecyclerview.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.activity.BaseActivity;
import pl.fzymek.advancedrecyclerview.adapter.ImagesAdapter;
import pl.fzymek.advancedrecyclerview.config.Config;
import pl.fzymek.advancedrecyclerview.controller.MainController;
import pl.fzymek.advancedrecyclerview.model.Image;
import pl.fzymek.advancedrecyclerview.ui.MainUI;
import pl.fzymek.advancedrecyclerview.utils.InsetDecoration;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static pl.fzymek.advancedrecyclerview.utils.Utils.hasApi;
import static pl.fzymek.advancedrecyclerview.utils.Utils.isLandscape;

/**
 * Created by Filip Zymek on 2015-06-18.
 */
public class MainFragment extends Fragment implements MainUI, SwipeRefreshLayout.OnRefreshListener {

	private final static String TAG = MainFragment.class.getSimpleName();
	private final static String CACHE = "cache";

	@InjectView(R.id.recycler)
	protected RecyclerView recyclerView;

	@InjectView(R.id.swipe_refresh)
	protected SwipeRefreshLayout swipeRefreshLayout;

	CacheFragment<Observable> cacheFragment;
	MainController controller;
	ImagesAdapter adapter;
	RecyclerView.LayoutManager linearLayoutManager;
	RecyclerView.LayoutManager gridLayoutManager;
	RecyclerView.LayoutManager staggeredLayoutManager;

	public static MainFragment newInstance() {
		MainFragment fragment = new MainFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setupCache();
		setupController(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.inject(this, view);
		setupRefresh();
		setupLayoutManagers();
		setupRecyclerView();
	}

	@Override
	public void onResume() {
		super.onResume();
		setupActionBar();
		controller.loadData();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ButterKnife.reset(this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_main, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		getStaggeredGridEnabled()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(isEnabled -> {
				MenuItem item = menu.findItem(R.id.action_staggered_grid);
				if (item != null) {
					item.setChecked(isEnabled);
				}
			});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		switch (id) {
			case R.id.action_settings:
				handleSettingsAction();
				return true;
			case R.id.action_staggered_grid:
				Log.d(TAG, "Clicked staggered grid action");
				handleStaggeredGridCheckAction(item);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onError(Throwable error) {
		Toast.makeText(getActivity(), getActivity().getApplicationContext().getString(R.string.error_happened), Toast.LENGTH_SHORT).show();
		Log.e("MainFragment", "Error: ", error);
	}

	@Override
	public void onLoadingStarted() {
		adapter.clear();
		swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));

	}

	@Override
	public void onLoadingStopped() {
		swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));
	}

	@Override
	public void onDisplayImages(List<Image> images) {
		adapter.setImages(images);
	}

	@Override
	public void onRefresh() {
		controller.refreshData();
	}

	private void setupCache() {
		//noinspection unchecked
		cacheFragment = (CacheFragment<Observable>) getFragmentManager().findFragmentByTag(CACHE);
		if (cacheFragment == null) {
			cacheFragment = new CacheFragment<>();
			getFragmentManager().beginTransaction().add(cacheFragment, CACHE).commit();
		}
	}

	private void setupController(Bundle savedState) {
		controller = new MainController(getActivity());
		controller.initialize(this);
		controller.restoreState(savedState);
		controller.setCache(cacheFragment);
	}

	private void setupRefresh() {
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setColorSchemeResources(
			android.R.color.holo_blue_bright,
			android.R.color.holo_green_light,
			android.R.color.holo_orange_light,
			android.R.color.holo_red_light
		);
	}

	private void setupLayoutManagers() {
		staggeredLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
		((GridLayoutManager) staggeredLayoutManager).setSpanSizeLookup(getSpanSizeLookup());

		gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);

		linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
	}

	private void setupRecyclerView() {
		if (isLandscape(getActivity())) {
			SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
			boolean isStaggered = prefs.getBoolean(Config.KEY_PREF_USE_STAGGERED_GRID, false);
			if (isStaggered) {
				recyclerView.setLayoutManager(staggeredLayoutManager);
			} else {
				recyclerView.setLayoutManager(gridLayoutManager);
			}

		} else {
			recyclerView.setLayoutManager(linearLayoutManager);
		}

		adapter = new ImagesAdapter(getActivity(), ((BaseActivity) getActivity()).getDisplayImageOptions());
		recyclerView.setAdapter(adapter);
		if (hasApi(Build.VERSION_CODES.LOLLIPOP)) {
			recyclerView.addItemDecoration(getItemDecoration());
		}
	}


	private void setupActionBar() {
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

	private void handleSettingsAction() {
		getFragmentManager().beginTransaction()
			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
			.replace(R.id.content_frame, SettingsFragment.newInstance(), "settings")
			.addToBackStack(null)
			.commit();
	}

	private void handleStaggeredGridCheckAction(final MenuItem item) {
		getStaggeredGridEnabled()
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(isEnabled -> {
				if (isEnabled) {
					item.setChecked(false);
					setStaggeredGridEnabled(false)
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe((Void v) -> recyclerView.setLayoutManager(gridLayoutManager));
				} else {
					item.setChecked(true);
					setStaggeredGridEnabled(true)
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe((Void v) -> recyclerView.setLayoutManager(staggeredLayoutManager));
				}
			});

	}

	private RecyclerView.ItemDecoration getItemDecoration() {
		return new InsetDecoration(getActivity());
	}


	private GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
		return new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return position % 3 == 0 ? 2 : 1;
			}
		};
	}

	private Observable<Boolean> getStaggeredGridEnabled() {
		return Observable.create(subscriber -> {
			SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
			subscriber.onNext(prefs.getBoolean(Config.KEY_PREF_USE_STAGGERED_GRID, false));
			subscriber.onCompleted();
		});
	}

	private Observable<Void> setStaggeredGridEnabled(boolean isEnabled) {
		return Observable.create(subscriber -> {
			SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
			prefs.edit().putBoolean(Config.KEY_PREF_USE_STAGGERED_GRID, isEnabled).apply();
			subscriber.onNext(null);
			subscriber.onCompleted();
		});
	}

}
