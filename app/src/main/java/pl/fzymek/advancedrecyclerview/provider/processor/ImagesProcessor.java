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
import pl.fzymek.advancedrecyclerview.provider.Processor;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class ImagesProcessor extends DatabaseProcessor {
	public ImagesProcessor(Context context, int code) {
		super(context, code, Contract.Images.TABLE_NAME);
	}

	@Override
	public void createTable(SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE " + name + " (")
			.append(Contract.Images._ID + " INTEGER PRIMARY KEY, ")
			.append(Contract.Images.ID + " TEXT UNIQUE, ")
			.append(Contract.Images.TITLE + " TEXT, ")
			.append(Contract.Images.ARTIST + " TEXT, ")
			.append(Contract.Images.CAPTION + " TEXT, ")
			.append(Contract.Images.COLLECTION_NAME + " TEXT, ")
			.append(Contract.Images.DATE_CREATED + " TEXT, ")
			.append(Contract.Images.VALIDITY + " INTEGER")
			.append(");")
			.append("CREATE UNIQUE INDEX id1 ON " + name +"(" + Contract.Images.ID +")");
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
