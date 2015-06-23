package pl.fzymek.advancedrecyclerview.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import pl.fzymek.advancedrecyclerview.provider.Contract;

/**
 * Created by Filip Zymek on 2015-06-08.
 */
public class Image implements DbModel {
	int _id;
	@SerializedName("id")
	String id;
	@SerializedName("caption")
	String caption;
	@SerializedName("title")
	String title;
	@SerializedName("artist")
	String artist;
	@SerializedName("collection_name")
	String collectionName;
	@SerializedName("date_created")
	String dateCreated;
	@SerializedName("display_sizes")
	List<DisplaySize> displaySizes;
	int validity;

	public int get_id() {
		return _id;
	}

	public String getId() {
		return id;
	}

	public String getCaption() {
		return caption;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public List<DisplaySize> getDisplaySizes() {
		return displaySizes;
	}

	public int getValidity() {
		return validity;
	}

	public DisplaySize getDisplayByType(DisplaySizeType type) {
		for (DisplaySize size : displaySizes) {
			if (size.getName().equals(type.name)) {
				return size;
			}
		}
		return null;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues(7);
		if (_id != 0) {
			values.put(Contract.Images._ID, _id);
		}
		values.put(Contract.Images.ID, id);
		values.put(Contract.Images.CAPTION, caption);
		values.put(Contract.Images.TITLE, title);
		values.put(Contract.Images.ARTIST, artist);
		values.put(Contract.Images.COLLECTION_NAME, collectionName);
		values.put(Contract.Images.DATE_CREATED, dateCreated);
		return values;
	}

	@Override
	public int hashCode() {
		return 29 * id.hashCode() + 19 *title.hashCode() + 17 * collectionName.hashCode() + 13 * dateCreated.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof Image) {
			Image image = (Image) o;
			return id.equals(image.getId())
				&& title.equals(image.getTitle())
				&& collectionName.equals(image.getCollectionName())
				&& dateCreated.equals(image.getDateCreated());
		}
		return false;
	}

	@Override
	public String toString() {
		return "Image{" +
			"_id=" + _id +
			", id='" + id + '\'' +
			", caption='" + caption + '\'' +
			", title='" + title + '\'' +
			", artist='" + artist + '\'' +
			", collectionName='" + collectionName + '\'' +
			", dateCreated='" + dateCreated + '\'' +
			", displaySizes=" + displaySizes +
			", validity=" + validity +
			'}';
	}

	public static Image fromCursor(Cursor cursor) {
		if (cursor == null) return null;

		Image img = new Image();
		img._id = cursor.getInt(cursor.getColumnIndex(Contract.Images._ID));
		img.id = cursor.getString(cursor.getColumnIndex(Contract.Images.ID));
		img.caption = cursor.getString(cursor.getColumnIndex(Contract.Images.CAPTION));
		img.title = cursor.getString(cursor.getColumnIndex(Contract.Images.TITLE));
		img.artist = cursor.getString(cursor.getColumnIndex(Contract.Images.ARTIST));
		img.collectionName = cursor.getString(cursor.getColumnIndex(Contract.Images.COLLECTION_NAME));
		img.dateCreated = cursor.getString(cursor.getColumnIndex(Contract.Images.DATE_CREATED));
		img.validity = cursor.getInt(cursor.getColumnIndex(Contract.Images.VALIDITY));

		return img;
	}


	public enum DisplaySizeType {
		THUMB("thumb"),
		PREVIEW("preview"),
		LARGE("comp");

		protected final String name;

		DisplaySizeType(String type) {
			this.name = type;
		}

	}
}
