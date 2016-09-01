package ilya.rsswidget.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

	private static final String PREFERENCE_NAME = "widget_preference";

	public static final String KEY_RSS_LINK = "rss_link";
	public static final String KEY_LAST_INDEX = "last_index";

	public static void put(Context context, String key, String value) {
		getPreferences(context).edit().putString(key, value).apply();
	}

	public static void put(Context context, String key, int value) {
		getPreferences(context).edit().putInt(key, value).apply();
	}

	public static String getString(Context context, String key, String defValue) {
		return getPreferences(context).getString(key, defValue);
	}

	public static int getInt(Context context, String key, int defValue) {
		return getPreferences(context).getInt(key, defValue);
	}

	public static void remove(Context context, String key) {
		getPreferences(context).edit().remove(key).apply();
	}

	public static void clear(Context context) {
		getPreferences(context).edit().clear().apply();
	}

	private static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}
}
