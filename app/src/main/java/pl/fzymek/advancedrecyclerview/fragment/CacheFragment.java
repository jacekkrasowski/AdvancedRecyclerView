package pl.fzymek.advancedrecyclerview.fragment;

import android.app.Fragment;
import android.os.Bundle;

import pl.fzymek.advancedrecyclerview.utils.SimpleCache;

/**
 * Created by Filip Zymek on 2015-06-12.
 */
public class CacheFragment<T> extends Fragment implements SimpleCache<T> {

	T cache;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void put(T o) {
		cache = o;
	}

	@Override
	public T get() {
		return cache;
	}
}
