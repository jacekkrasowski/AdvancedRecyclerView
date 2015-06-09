package pl.fzymek.advancedrecyclerview.controller;


import pl.fzymek.advancedrecyclerview.ui.UILifecycleObserver;

public interface Controller<UI> extends UILifecycleObserver {
	void initialize(UI ui);
	void saveState(Object outState);
	void restoreState(Object savedState);
}