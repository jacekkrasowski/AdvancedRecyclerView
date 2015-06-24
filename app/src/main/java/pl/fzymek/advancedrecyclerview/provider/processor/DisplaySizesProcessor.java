package pl.fzymek.advancedrecyclerview.provider.processor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

import pl.fzymek.advancedrecyclerview.config.Config;
import pl.fzymek.advancedrecyclerview.provider.Contract;
import pl.fzymek.advancedrecyclerview.provider.DatabaseProcessor;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class DisplaySizesProcessor extends DatabaseProcessor {
	public DisplaySizesProcessor(Context context, int code) {
		super(context, code, Contract.DisplaySizes.TABLE_NAME);
	}

	@Override
	public void createTable(SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE " + name + " (")
			.append(Contract.DisplaySizes._ID + " INTEGER PRIMARY KEY, ")
			.append(Contract.DisplaySizes.IMAGE_ID + " TEXT, ")
			.append(Contract.DisplaySizes.NAME + " TEXT, ")
			.append(Contract.DisplaySizes.URI + " TEXT, ")
			.append(Contract.DisplaySizes.VALIDITY + " INTEGER, ")
			.append("FOREIGN KEY (" + Contract.DisplaySizes.IMAGE_ID + ") " +
				"REFERENCES " + Contract.Images.TABLE_NAME + "(" + Contract.Images.ID + ") ON DELETE CASCADE")
			.append(")");
		db.execSQL(sql.toString());
	}

	@Override
	public void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	@Override
	public Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		purge(db);

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(name);
		Cursor c = queryBuilder.query(
			db,
			projection,
			selection,
			selectionArgs,
			null,
			null,
			sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), Contract.Images.CONTENT_URI);

		return c;
	}

	@Override
	public Uri insert(SQLiteDatabase db, Uri uri, ContentValues values) {
		long validity = Calendar.getInstance().getTimeInMillis() + Config.DEFAULT_VALIDITY;
		ContentValues valuesWithValidity = new ContentValues(values);
		valuesWithValidity.put(Contract.Images.VALIDITY, validity);

		long id = db.insertWithOnConflict(name, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		return Uri.parse(Contract.Images.CONTENT_URI + "/" + id);
	}
}
