package pl.fzymek.advancedrecyclerview.activity;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.config.API;
import pl.fzymek.advancedrecyclerview.controller.MainController;
import pl.fzymek.advancedrecyclerview.model.Image;
import pl.fzymek.advancedrecyclerview.model.Result;
import pl.fzymek.advancedrecyclerview.network.FiveHundredPxAPI;
import pl.fzymek.advancedrecyclerview.network.RetrofitHttpOAuthConsumer;
import pl.fzymek.advancedrecyclerview.network.SigningOkClient;
import pl.fzymek.advancedrecyclerview.ui.MainUI;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import rx.Observable;
import rx.Subscriber;


public class MainActivity extends AppCompatActivity implements MainUI {

//	@InjectView(R.id.recycler)
//	protected RecyclerView recyclerView;
	@InjectView(R.id.toolbar)
	protected Toolbar toolbar;

	MainController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);
		setupController(savedInstanceState);
		setupToolbar();
		setupRecyclerView();


		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {



				RestAdapter adapter = new RestAdapter.Builder()
					.setEndpoint(API.FIVE_HUNDRED_API_ENDPOINT)
					.build();

				FiveHundredPxAPI service = adapter.create(FiveHundredPxAPI.class);
				Observable<Result> images = service.getImages();
				images.subscribe(new Subscriber<Result>() {
					@Override
					public void onCompleted() {
						Log.d("MainActivity", "onCompleted");
					}

					@Override
					public void onError(Throwable e) {
						Log.d("MainActivity", "onError", e);
					}

					@Override
					public void onNext(Result s) {
						Log.d("MainActivity", "onNext: \n"+ s);
					}
				});

				return null;
			}
		}.execute();
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
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onError(Throwable error) {

	}

	@Override
	public void onLoadingStarted() {

	}

	@Override
	public void onLoadingStopped() {

	}

	@Override
	public void onDisplayImages(List<Image> images) {

	}

	private void setupController(Bundle savedState) {
		controller = new MainController(this);
		controller.initialize(this);
		controller.restoreState(savedState);
	}

	private void setupRecyclerView() {

	}

	private void setupToolbar() {
		setSupportActionBar(toolbar);
	}

}
