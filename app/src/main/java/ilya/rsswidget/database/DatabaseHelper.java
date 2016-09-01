package ilya.rsswidget.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "il.rss.widget.db";
	public static final int DATABASE_VERSION = 1;

	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	public static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + DatabaseEntry.TABLE_NAME + " (" +
					DatabaseEntry._ID + " INTEGER PRIMARY KEY," +
					DatabaseEntry.COLUMN_NAME_WIDGET_ID + INTEGER_TYPE + COMMA_SEP +
					DatabaseEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
					DatabaseEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE +
					" );";

	public static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + DatabaseEntry.TABLE_NAME;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}
}