package pl.fzymek.advancedrecyclerview.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public interface DatabaseInterface {

	void createTable(SQLiteDatabase db);

	void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion);

	Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);

	Uri insert(SQLiteDatabase db, Uri uri, ContentValues values);

	int delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs);

	int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs);
}
