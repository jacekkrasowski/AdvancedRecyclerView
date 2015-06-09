package pl.fzymek.advancedrecyclerview.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.controller.MainController;
import pl.fzymek.advancedrecyclerview.model.Image;
import pl.fzymek.advancedrecyclerview.ui.MainUI;


public class MainActivity extends AppCompatActivity implements MainUI {

	@InjectView(R.id.recycler)
	protected RecyclerView recyclerView;

	MainController controller;
	RecyclerView.Adapter adapter;
	RecyclerView.LayoutManager layoutManager;
	ImageLoaderConfiguration config;
	DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);
		setupActionBar();
		setupPreferences();
		setupController(savedInstanceState);
		setupRecyclerView();
		setupImageLoader();
	}

	@Override
	protected void onResume() {
		super.onResume();
		controller.loadData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.reset(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onError(Throwable error) {
		Toast.makeText(this, getString(R.string.error_happened), Toast.LENGTH_SHORT).show();
		Log.e("MainActivity", "Error: ", error);
	}

	@Override
	public void onLoadingStarted() {
		Log.d("MainActivity", "onLoadingStarted");
	}

	@Override
	public void onLoadingStopped() {
		Log.d("MainActivity", "onLoadingStopped");
	}

	@Override
	public void onDisplayImages(List<Image> images) {
		Log.d("MainActivity", "onDisplayImages");
		((ImagesAdapter) adapter).setImages(images);
	}

	private void setupImageLoader() {
		int memCacheSize = 2 * 1024 * 1024;
		config = new ImageLoaderConfiguration.Builder(this)
			.memoryCache(new LruMemoryCache(memCacheSize))
			.memoryCacheSize(memCacheSize)
			.build();
		ImageLoader.getInstance().init(config);
		options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisk(false)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new FadeInBitmapDisplayer(100))
			.build();
	}

	private void setupPreferences() {
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
	}

	private void setupController(Bundle savedState) {
		controller = new MainController(this);
		controller.initialize(this);
		controller.restoreState(savedState);
	}

	private void setupRecyclerView() {
		layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		adapter = new ImagesAdapter();
		recyclerView.setAdapter(adapter);
	}

	private void setupActionBar() {
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}


	private static class ImagesAdapter extends RecyclerView.Adapter<ImageCard> {

		List<Image> images;

		public ImagesAdapter() {
			images = new ArrayList<>();
		}

		@Override
		public ImageCard onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, parent, false);
			return new ImageCard(v);
		}

		@Override
		public void onBindViewHolder(ImageCard holder, int position) {
			holder.artist.setText(getItem(position).getArtist());
			holder.title.setText(getItem(position).getTitle());
			//load image
			ImageLoader.getInstance().displayImage(getItem(position).getDisplaySizes().get(0).getUri(), holder.image);
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
	}

	private static class ImageCard extends RecyclerView.ViewHolder {

		ImageView image;
		TextView artist;
		TextView title;

		public ImageCard(View itemView) {
			super(itemView);
			image = (ImageView) itemView.findViewById(R.id.image);
			artist = (TextView) itemView.findViewById(R.id.artist);
			title = (TextView) itemView.findViewById(R.id.title);
		}
	}
}
