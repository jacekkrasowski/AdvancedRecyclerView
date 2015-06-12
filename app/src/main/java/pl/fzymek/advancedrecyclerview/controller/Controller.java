package pl.fzymek.advancedrecyclerview.controller;


import java.util.TreeSet;

import pl.fzymek.advancedrecyclerview.ui.UILifecycleObserver;
import pl.fzymek.advancedrecyclerview.utils.SimpleCache;

public interface Controller<UI> extends UILifecycleObserver {
	void initialize(UI ui);
	void saveState(Object outState);
	void restoreState(Object savedState);
	<T> void setCache(SimpleCache<T> cache);
	<T> SimpleCache<T> getCache();
	boolean hasCache();
}