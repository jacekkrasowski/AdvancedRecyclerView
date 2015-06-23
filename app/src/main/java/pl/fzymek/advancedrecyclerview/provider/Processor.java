package pl.fzymek.advancedrecyclerview.provider;

import android.content.Context;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public abstract class Processor implements DatabaseInterface {

	protected String name;
	protected int code;
	protected Context context;

	public Processor(Context context, int code, String name) {
		this(context, code);
		this.name = name;
	}

	public Processor(Context context, int code) {
		this.context = context;
		this.code = code;
	}

	public Processor() {

	}


	public boolean containsCode(int code) {
		return this.code == code;
	}

	protected Context getContext() {
		return context;
	}

	public void onPreBulkInsert() {}
}
