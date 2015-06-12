package pl.fzymek.advancedrecyclerview.utils;

/**
 * Created by Filip Zymek on 2015-06-12.
 */
public interface SimpleCache<T> {
	void put(Object key, T cachedObject);
	T get(Object key);
}
