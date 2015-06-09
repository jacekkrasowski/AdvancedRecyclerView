package pl.fzymek.advancedrecyclerview.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Filip Zymek on 2015-06-08.
 */
public class Image {

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

	@Override
	public String toString() {
		return "Image{" +
			"id='" + id + '\'' +
			", caption='" + caption + '\'' +
			", title='" + title + '\'' +
			", artist='" + artist + '\'' +
			", collectionName='" + collectionName + '\'' +
			", dateCreated='" + dateCreated + '\'' +
			", displaySizes=" + displaySizes +
			'}';
	}
}
