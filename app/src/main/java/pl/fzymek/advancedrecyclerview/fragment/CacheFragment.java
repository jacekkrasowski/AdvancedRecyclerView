package pl.fzymek.advancedrecyclerview.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.LruCache;

import pl.fzymek.advancedrecyclerview.utils.SimpleCache;

/**
 * Created by Filip Zymek on 2015-06-12.
 */
public class CacheFragment<T> extends Fragment implements SimpleCache<T> {

	LruCache<Object, T> cache;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		cache = new LruCache<>(3);
	}

	@Override
	public void put(Object key, T o) {
		cache.put(key, o);
	}

	@Override
	public T get(Object key) {
		return cache.get(key);
	}
}
