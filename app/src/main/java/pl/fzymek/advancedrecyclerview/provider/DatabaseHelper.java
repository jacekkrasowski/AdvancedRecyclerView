package pl.fzymek.advancedrecyclerview.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;

import java.util.List;

import pl.fzymek.advancedrecyclerview.config.Config;
import pl.fzymek.advancedrecyclerview.utils.Utils;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private final static String TAG = DatabaseHelper.class.getSimpleName();

	private final List<Processor> tables;

	public DatabaseHelper(Context context, List<Processor> tables) {
		super(context, Config.DB_NAME, (db, masterQuery, editTable, query) -> {
			Log.d("SQL", query.toString());
			return new SQLiteCursor(masterQuery, editTable, query);
		}, Config.DB_VERSION);
		this.tables = tables;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			for (int i = 0; i < tables.size(); i++) {
				Processor processor = tables.get(i);
				processor.createTable(db);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.beginTransaction();
		try {
			for (int i = 0; i < tables.size(); i++) {
				Processor processor = tables.get(i);
				processor.upgradeTable(db, oldVersion, newVersion);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			Log.d(TAG, "enabling foreign keys");
			if (Utils.hasApi(Build.VERSION_CODES.JELLY_BEAN)) {
				db.setForeignKeyConstraintsEnabled(true);
			} else {
				db.execSQL("PRAGMA foreign_keys = ON;");
			}
		}
	}
}
