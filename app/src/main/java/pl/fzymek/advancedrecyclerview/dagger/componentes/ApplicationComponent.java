package pl.fzymek.advancedrecyclerview.dagger.componentes;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import javax.inject.Singleton;

import dagger.Component;
import pl.fzymek.advancedrecyclerview.activity.BaseActivity;
import pl.fzymek.advancedrecyclerview.dagger.modules.ApplicationModule;

/**
 * Created by Filip Zymek on 2015-06-19.
 */
@Singleton
@Component (modules = ApplicationModule.class)
public interface ApplicationComponent {
	Context getContext();

	void inject(BaseActivity baseActivity);
}
