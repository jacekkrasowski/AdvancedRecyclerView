package pl.fzymek.advancedrecyclerview.fragment;

import android.animation.TimeInterpolator;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.activity.MainActivity;
import pl.fzymek.advancedrecyclerview.controller.MainController;
import pl.fzymek.advancedrecyclerview.model.Image;
import pl.fzymek.advancedrecyclerview.ui.MainUI;
import rx.Observable;

/**
 * Created by Filip Zymek on 2015-06-18.
 */
public class MainFragment extends Fragment implements MainUI, SwipeRefreshLayout.OnRefreshListener {

	private final static String CACHE = "cache";
	CacheFragment<Observable> cacheFragment;

	@InjectView(R.id.recycler)
	protected RecyclerView recyclerView;
	@InjectView(R.id.swipe_refresh)
	protected SwipeRefreshLayout swipeRefreshLayout;

	MainController controller;
	ImagesAdapter adapter;
	RecyclerView.LayoutManager layoutManager;

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
		setupRecyclerView();
	}

	@Override
	public void onResume() {
		super.onResume();
		setupActionBar();
		Log.d("MainFragment", "start loading data");
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		switch (id) {
			case R.id.action_settings:
				getFragmentManager().beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.replace(R.id.content_frame, SettingsFragment.newInstance(), "settings")
					.addToBackStack(null)
					.commit();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onError(Throwable error) {
		Toast.makeText(getActivity(), getString(R.string.error_happened), Toast.LENGTH_SHORT).show();
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

	private void setupRefresh() {
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setColorSchemeResources(
			android.R.color.holo_blue_bright,
			android.R.color.holo_green_light,
			android.R.color.holo_orange_light,
			android.R.color.holo_red_light
		);
	}


	private void setupCache() {
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

	private void setupRecyclerView() {
		if (isLandscape(getActivity())) {
			layoutManager = new GridLayoutManager(getActivity(), 2);
		} else {
			layoutManager = new LinearLayoutManager(getActivity());
		}

		recyclerView.setLayoutManager(layoutManager);
		adapter = new ImagesAdapter(getActivity(), ((MainActivity)getActivity()).getDisplayImageOptions());
		recyclerView.setAdapter(adapter);
		recyclerView.setItemAnimator(getItemAnimator());
	}

	private RecyclerView.ItemAnimator getItemAnimator() {
		return null;
	}

	private void setupActionBar() {
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

	private static boolean isLandscape(Context c) {
		int orientation = c.getResources().getConfiguration().orientation;
		return orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	@Override
	public void onRefresh() {
		controller.refreshData();
	}


	private static class ImagesAdapter extends RecyclerView.Adapter<ImageCard> {

		List<Image> images;
		Context context;
		int lastPosition = 0;
		Point windowSize = new Point();
		SparseBooleanArray animatedPositions = new SparseBooleanArray();
		TimeInterpolator interpolator;
		DisplayImageOptions options;

		public ImagesAdapter(Context context, DisplayImageOptions options) {
			this.context = context;
			this.options = options;
			images = new ArrayList<>();
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Point windowSize = new Point();
			wm.getDefaultDisplay().getSize(windowSize);
			interpolator = new DecelerateInterpolator(2);
		}

		@Override
		public ImageCard onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, parent, false);
			return new ImageCard(v);
		}

		@Override
		public void onBindViewHolder(final ImageCard holder, int position) {
			holder.artist.setText(getItem(position).getArtist());
			holder.title.setText(getItem(position).getTitle());
			ImageLoader.getInstance().cancelDisplayTask(holder.image);
			ImageLoader.getInstance().displayImage(getItem(position).getDisplayByType(Image.DisplaySizeType.PREVIEW).getUri(), holder.image, options);

			if (position > lastPosition && !animatedPositions.get(position)) {
				lastPosition = position;
				animatedPositions.put(position, true);
				startItemAnimation(holder, position);
			}
		}

		private void startItemAnimation(ImageCard holder, int position) {
			setupCommonItemProperties(holder);

			if (isLandscape(context)) {
				setupHorizontalItemProperties(holder, position);
			} else {
				setupVerticalItemProperties(holder);
			}

			holder.itemView.animate().translationX(0)
				.translationY(0)
				.rotationX(0)
				.rotationY(0)
				.scaleX(1)
				.scaleY(1)
				.alpha(1)
				.setDuration(300)
				.setInterpolator(interpolator)
				.setStartDelay(0)
				.start();
		}

		private Image getItem(int position) {
			return images.get(position);
		}

		@Override
		public int getItemCount() {
			return images.size();
		}

		public void setImages(List<Image> images) {
			this.images.clear();
			this.images.addAll(images);
			notifyDataSetChanged();
		}

		public void clear() {
			this.images.clear();
			lastPosition = 0;
			animatedPositions.clear();
			notifyDataSetChanged();
		}

		private void setupCommonItemProperties(ImageCard holder) {
			holder.itemView.setTranslationX(0);
			holder.itemView.setTranslationY(windowSize.y);
			holder.itemView.setScaleX(0.6f);
			holder.itemView.setScaleY(0.6f);
			holder.itemView.setAlpha(0);
		}

		private void setupHorizontalItemProperties(ImageCard holder, int position) {
			if (position % 2 == 0) {
				holder.itemView.setRotationY(45.0f);
			} else {
				holder.itemView.setRotationY(-45.0f);
			}
		}

		private void setupVerticalItemProperties(ImageCard holder) {
			holder.itemView.setRotationX(45.0f);
		}

	}

	private static class ImageCard extends RecyclerView.ViewHolder {

		CardView cardView;
		ImageView image;
		TextView artist;
		TextView title;
		ProgressBar progress;

		public ImageCard(View itemView) {
			super(itemView);
			cardView = (CardView) itemView.findViewById(R.id.card);
			image = (ImageView) itemView.findViewById(R.id.image);
			artist = (TextView) itemView.findViewById(R.id.artist);
			title = (TextView) itemView.findViewById(R.id.title);
			progress = (ProgressBar) itemView.findViewById(R.id.progress);
		}
	}
}
