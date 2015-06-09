package pl.fzymek.advancedrecyclerview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import butterknife.ButterKnife;
import pl.fzymek.advancedrecyclerview.R;
import pl.fzymek.advancedrecyclerview.controller.MainController;
import pl.fzymek.advancedrecyclerview.model.Image;
import pl.fzymek.advancedrecyclerview.ui.MainUI;


public class MainActivity extends AppCompatActivity implements MainUI {

//	@InjectView(R.id.recycler)
//	protected RecyclerView recyclerView;

	MainController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);
		setupActionBar();
		setupPreferences();
		setupController(savedInstanceState);
		setupRecyclerView();
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

	private void setupPreferences() {
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
	}

	private void setupController(Bundle savedState) {
		controller = new MainController(this);
		controller.initialize(this);
		controller.restoreState(savedState);
	}

	private void setupRecyclerView() {

	}

	private void setupActionBar() {
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

}
