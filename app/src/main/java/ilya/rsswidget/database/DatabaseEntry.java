package ilya.rsswidget.database;

import android.provider.BaseColumns;

public final class DatabaseEntry implements BaseColumns {
	public static final String TABLE_NAME = "rss";
	public static final String COLUMN_NAME_WIDGET_ID = "widget_id";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_DESCRIPTION = "description";
}
