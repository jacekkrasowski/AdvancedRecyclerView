package pl.fzymek.advancedrecyclerview.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Looper;

import java.util.Calendar;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public abstract class DatabaseProcessor extends Processor {

	public DatabaseProcessor(Context context, int code, String name) {
		super(context, code, name);
	}

	public DatabaseProcessor(Context context, int code) {
		super(context, code);
	}

	@Override
	public int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return db.update(name, values, selection, selectionArgs);
	}

	@Override
	public int delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
		return db.delete(name, selection, selectionArgs);
	}

	protected void purge(SQLiteDatabase db) {
		long now = Calendar.getInstance().getTimeInMillis();
		db.delete(name, Contract.VALIDITY + "<= ?", new String[]{Long.toString(now)});
	}
}
