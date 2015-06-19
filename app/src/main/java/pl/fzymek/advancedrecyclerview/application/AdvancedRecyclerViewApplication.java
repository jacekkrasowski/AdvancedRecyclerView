package pl.fzymek.advancedrecyclerview.application;

import android.app.Application;

import pl.fzymek.advancedrecyclerview.dagger.componentes.ApplicationComponent;
import pl.fzymek.advancedrecyclerview.dagger.componentes.DaggerApplicationComponent;
import pl.fzymek.advancedrecyclerview.dagger.modules.ApplicationModule;

/**
 * Created by Filip Zymek on 2015-06-19.
 */
public class AdvancedRecyclerViewApplication extends Application {

	private ApplicationComponent applicationComponent;

	@Override
	public void onCreate() {
		super.onCreate();

		this.applicationComponent = DaggerApplicationComponent.builder()
			.applicationModule(new ApplicationModule(this))
			.build();
	}

	public ApplicationComponent getApplicationComponent() {
		return applicationComponent;
	}
}
