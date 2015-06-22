package pl.fzymek.advancedrecyclerview.provider;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

import java.util.List;

import pl.fzymek.advancedrecyclerview.config.Config;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private final List<Processor> tables;

	public DatabaseHelper(Context context, List<Processor> tables) {
		super(context, Config.DB_NAME, null, Config.DB_VERSION);
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
}
