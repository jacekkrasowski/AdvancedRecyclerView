package pl.fzymek.advancedrecyclerview.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import pl.fzymek.advancedrecyclerview.provider.Contract;

/**
 * Created by Filip Zymek on 2015-06-09.
 */
public class DisplaySize implements DbModel{
	int _id;
	String imageId;
	@SerializedName("name")
	String name;
	@SerializedName("uri")
	String uri;
	int validity;

	public int get_id() {
		return _id;
	}

	public String getImageId() {
		return imageId;
	}

	public String getName() {
		return name;
	}

	public String getUri() {
		return uri;
	}

	public int getValidity() {
		return validity;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues(3);
		if (_id != 0) {
			values.put(Contract.DisplaySizes._ID, _id);
		}
		values.put(Contract.DisplaySizes.NAME, name);
		values.put(Contract.DisplaySizes.URI, uri);
		return values;
	}

	@Override
	public int hashCode() {
		return 17 * name.hashCode() + 13 * uri.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof DisplaySize) {
			DisplaySize displaySize = (DisplaySize) o;
			return name.equals(displaySize.getName()) && uri.equals(displaySize.getUri());
		}
		return false;
	}

	@Override
	public String toString() {
		return "DisplaySize{" +
			"_id=" + _id +
			", imageId=" + imageId +
			", name='" + name + '\'' +
			", uri='" + uri + '\'' +
			", validity=" + validity +
			'}';
	}

	public static DisplaySize fromCursor(Cursor displaySizesCursor) {
		if (displaySizesCursor == null) return null;

		DisplaySize size = new DisplaySize();
		size._id = displaySizesCursor.getInt(displaySizesCursor.getColumnIndex(Contract.DisplaySizes._ID));
		size.imageId = displaySizesCursor.getString(displaySizesCursor.getColumnIndex(Contract.DisplaySizes.IMAGE_ID));
		size.name = displaySizesCursor.getString(displaySizesCursor.getColumnIndex(Contract.DisplaySizes.NAME));
		size.uri = displaySizesCursor.getString(displaySizesCursor.getColumnIndex(Contract.DisplaySizes.URI));
		size.validity = displaySizesCursor.getInt(displaySizesCursor.getColumnIndex(Contract.DisplaySizes.VALIDITY));

		return size;
	}
}
