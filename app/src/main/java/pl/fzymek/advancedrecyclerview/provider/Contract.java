package pl.fzymek.advancedrecyclerview.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import pl.fzymek.advancedrecyclerview.model.DisplaySize;

/**
 * Created by Filip Zymek on 2015-06-22.
 */
public final class Contract {

	public static final String AUTHORITY = "pl.fzymek.advancedrecyclerview.provider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public static final String VALIDITY = "validity";

	protected interface SyncColumns {
		/**
		 * validity
		 * Type: INTEGER
		 */
		String VALIDITY = Contract.VALIDITY;
	}

	protected interface DisplaySizeColumns extends SyncColumns{

		/**
		 * image id
		 * Type: INTEGER
		 */
		String IMAGE_ID = "image_id";

		/**
		 * name
		 * Type: TEXT
		 */
		String NAME = "name";

		/**
		 * uri
		 * Type: Text
		 */
		String URI = "uri";
	}


	protected interface ImageColumns extends SyncColumns {

		/**
		 * artist
		 * Type: TEXT
		 */
		String ARTIST = "artist";

		/**
		 * caption
		 * Type: TEXT
		 */
		String CAPTION = "caption";

		/**
		 * collection name
		 * Type: TEXT
		 */
		String COLLECTION_NAME = "collection_name";

		/**
		 * date created
		 * Type: TEXT
		 */
		String DATE_CREATED = "date_created";

		/**
		 * id
		 * Type: INTEGER
		 */
		String ID = "id";

		/**
		 * title
		 * Type: TEXT
		 */
		String TITLE = "title";
	}


	public static final class DisplaySizes implements BaseColumns, DisplaySizeColumns {
		public static final String TABLE_NAME = "display_sizes";
		public static final Uri CONTENT_URI = Uri.parse(Contract.CONTENT_URI + "/" + TABLE_NAME);
		public static final String[] TABLE_COLUMNS = {
			_ID,
			IMAGE_ID,
			NAME,
			URI,
			VALIDITY
		};

		private DisplaySizes() {}
	}

	public static final class Images implements BaseColumns, ImageColumns {
		public static final String TABLE_NAME = "images";
		public static final Uri CONTENT_URI = Uri.parse(Contract.CONTENT_URI + "/" + TABLE_NAME);
		public static final String[] TABLE_COLUMNS = {
			_ID,
			ID,
			TITLE,
			ARTIST,
			CAPTION,
			COLLECTION_NAME,
			DATE_CREATED,
			VALIDITY
		};

		private Images() {
		}
	}
}
