package pl.fzymek.advancedrecyclerview.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.util.LinkedList;
import java.util.List;

import pl.fzymek.advancedrecyclerview.provider.processor.DisplaySizesProcessor;
import pl.fzymek.advancedrecyclerview.provider.processor.ImagesProcessor;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public class Provider extends ContentProvider {

	public static final int IMAGES = 1;
	public static final int DISPLAY_SIZES = 2;

	private final Object lock = new Object();

	private static List<Processor> tables;
	private static final UriMatcher URI_MATCHER;

	private DatabaseHelper helper;

	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(Contract.AUTHORITY, Contract.Images.TABLE_NAME, IMAGES);
		URI_MATCHER.addURI(Contract.AUTHORITY, Contract.Images.TABLE_NAME + "/*", IMAGES);
		URI_MATCHER.addURI(Contract.AUTHORITY, Contract.DisplaySizes.TABLE_NAME, DISPLAY_SIZES);
		URI_MATCHER.addURI(Contract.AUTHORITY, Contract.DisplaySizes.TABLE_NAME + "/*", DISPLAY_SIZES);

	}

	@Override
	public boolean onCreate() {
		tables = new LinkedList<>();
		tables.add(new ImagesProcessor(getContext(), IMAGES));
		tables.add(new DisplaySizesProcessor(getContext(), DISPLAY_SIZES));

		helper = new DatabaseHelper(getContext(), tables);

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		synchronized (lock) {
			Processor processor = findProcessor(uri);
			return processor.query(helper.getWritableDatabase(), uri, projection, selection, selectionArgs, sortOrder);
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		synchronized (lock) {
			Processor processor = findProcessor(uri);
			Uri result = processor.insert(helper.getWritableDatabase(), uri, values);

			if (result != null) {
				getContext().getContentResolver().notifyChange(uri, null, false);
			}

			return result;
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		synchronized (lock) {
			Processor processor = findProcessor(uri);
			int count = processor.delete(helper.getWritableDatabase(), uri, selection, selectionArgs);

			getContext().getContentResolver().notifyChange(uri, null, false);

			return count;
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		synchronized (lock) {
			Processor processor = findProcessor(uri);
			int count = processor.update(helper.getWritableDatabase(), uri, values, selection, selectionArgs);

			getContext().getContentResolver().notifyChange(uri, null, false);

			return count;
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {

		synchronized (lock) {
			Processor processor = findProcessor(uri);
			processor.onPreBulkInsert();
			for (int i = 0; i < values.length; i++) {
				processor.insert(helper.getWritableDatabase(), uri, values[i]);
			}

			getContext().getContentResolver().notifyChange(uri, null, false);
		}

		return values.length;
	}

	protected Processor findProcessor(Uri uri) {
		int code = URI_MATCHER.match(uri);
		for (Processor processor : tables) {
			if (processor.containsCode(code)) {
				return processor;
			}
		}
		throw new IllegalArgumentException("Unknown URI" + uri);
	}
}
