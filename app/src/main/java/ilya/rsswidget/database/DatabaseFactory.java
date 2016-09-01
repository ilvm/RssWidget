package ilya.rsswidget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ilya.rsswidget.dto.RssItem;

import java.util.ArrayList;
import java.util.List;

public enum DatabaseFactory {

	INSTANCE;

	private SQLiteDatabase mDatabase;

	public void init(Context context) {
		mDatabase = new DatabaseHelper(context).getReadableDatabase();
	}

	public void insertRss(String widgetId, List<RssItem> rssList) {
		for (final RssItem item : rssList) {
			final ContentValues values = new ContentValues();
			values.put(DatabaseEntry.COLUMN_NAME_WIDGET_ID, widgetId);
			values.put(DatabaseEntry.COLUMN_NAME_TITLE, item.getTitle());
			values.put(DatabaseEntry.COLUMN_NAME_DESCRIPTION, item.getDescription());
			mDatabase.insert(DatabaseEntry.TABLE_NAME, null, values);
		}
	}

	public void deleteByWidgetId(String id) {
		mDatabase.delete(DatabaseEntry.TABLE_NAME, DatabaseEntry.COLUMN_NAME_WIDGET_ID + "=?", new String[]{id});
	}

	public List<RssItem> getRssLisByWidgetId(String widgetId) {

		final String[] projection = {
				DatabaseEntry.COLUMN_NAME_TITLE,
				DatabaseEntry.COLUMN_NAME_DESCRIPTION
		};

		final Cursor cursor = mDatabase.query(DatabaseEntry.TABLE_NAME, projection,
				DatabaseEntry.COLUMN_NAME_WIDGET_ID + "=?", new String[]{widgetId}, null, null, null);
		if (cursor.getCount() == 0) {
			return null;
		}

		final List<RssItem> result = new ArrayList<>();
		cursor.moveToFirst();
		do {
			result.add(new RssItem(cursor.getString(cursor.getColumnIndex(DatabaseEntry.COLUMN_NAME_TITLE)),
					cursor.getString(cursor.getColumnIndex(DatabaseEntry.COLUMN_NAME_DESCRIPTION))));
		} while (cursor.moveToNext());

		cursor.close();
		return result;
	}

	public RssItem getRssByWidgetId(String widgetId, int index) {

		final String[] projection = {
				DatabaseEntry.COLUMN_NAME_TITLE,
				DatabaseEntry.COLUMN_NAME_DESCRIPTION
		};

		final Cursor cursor = mDatabase.query(DatabaseEntry.TABLE_NAME, projection,
				DatabaseEntry.COLUMN_NAME_WIDGET_ID + "=?", new String[]{widgetId}, null, null, null);

		cursor.moveToFirst();
		try {
			if (cursor.move(index)) {
				return new RssItem(cursor.getString(cursor.getColumnIndex(DatabaseEntry.COLUMN_NAME_TITLE)),
						cursor.getString(cursor.getColumnIndex(DatabaseEntry.COLUMN_NAME_DESCRIPTION)));
			}
		} finally {
			cursor.close();
		}

		return null;
	}

	public void createTable() {
		mDatabase.execSQL(DatabaseHelper.SQL_CREATE_ENTRIES);
	}

	public void dropTable() {
		mDatabase.execSQL(DatabaseHelper.SQL_DELETE_ENTRIES);
	}
}
