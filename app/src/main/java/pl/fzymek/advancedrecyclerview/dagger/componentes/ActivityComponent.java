package pl.fzymek.advancedrecyclerview.dagger.componentes;

import android.app.Activity;

import dagger.Component;
import pl.fzymek.advancedrecyclerview.activity.BaseActivity;
import pl.fzymek.advancedrecyclerview.dagger.modules.ActivityModule;
import pl.fzymek.advancedrecyclerview.dagger.scope.PerActivity;

/**
 * Created by Filip Zymek on 2015-06-19.
 */
@PerActivity
@Component (dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
	Activity getActivity();
}
