package pl.fzymek.advancedrecyclerview.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Filip Zymek on 2015-06-08.
 */
public class Image {

	@SerializedName("id")
	String id;

	@Override
	public String toString() {
		return "Image{" +
			"id='" + id + '\'' +
			'}';
	}
}
