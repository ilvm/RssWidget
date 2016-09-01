package ilya.rsswidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import ilya.rsswidget.database.DatabaseFactory;
import ilya.rsswidget.dto.RssItem;
import ilya.rsswidget.utils.PreferenceManager;
import ilya.rsswidget.utils.ScheduleManager;

public class RssWidgetProvider extends AppWidgetProvider {

	private static final String ACTION_CLICK_PREV = "click_prev";
	private static final String ACTION_CLICK_NEXT = "click_next";
	public static final String ACTION_RESPONSE_SUCCESS = "ilya.rsswidget.RESPONSE_SUCCESS";
	public static final String ACTION_RESPONSE_FAIL = "ilya.rsswidget.RESPONSE_FAIL";
	public static final String KEY_EXTRA_RSS_ITEM = "rss_item";

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		for (int widgetId : appWidgetIds) {
			PreferenceManager.remove(context, PreferenceManager.KEY_RSS_LINK + widgetId);
			PreferenceManager.remove(context, PreferenceManager.KEY_LAST_INDEX + widgetId);
			DatabaseFactory.INSTANCE.deleteByWidgetId(Integer.toString(widgetId));
			ScheduleManager.cancel(context, widgetId);
		}
	}

	@Override
	public void onDisabled(Context context) {
		context.stopService(new Intent(context, LoadService.class));
		PreferenceManager.clear(context);
		super.onDisabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		final int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		final String action = intent.getAction();
		switch (action) {
		case ACTION_CLICK_NEXT:
			onClickNext(context, widgetId);
			break;
		case ACTION_CLICK_PREV:
			onClickPrev(context, widgetId);
			break;
		case ACTION_RESPONSE_SUCCESS:
			final RssItem item = (RssItem) intent.getSerializableExtra(KEY_EXTRA_RSS_ITEM);
			PreferenceManager.remove(context, PreferenceManager.KEY_LAST_INDEX + widgetId);
			updateWidget(context, widgetId, item);
			Log.d("ololo", "widgetId = " + widgetId + " Rss: " + item);
			break;
		case ACTION_RESPONSE_FAIL:
			errorWidget(context, widgetId);
			PreferenceManager.remove(context, PreferenceManager.KEY_LAST_INDEX + widgetId);
			break;
		}
	}

	protected void onClickNext(Context context, int widgetId) {
		int index = PreferenceManager.getInt(context, PreferenceManager.KEY_LAST_INDEX + widgetId, 0);
		final RssItem item = DatabaseFactory.INSTANCE.getRssByWidgetId(Integer.toString(widgetId), ++index);
		if (item != null) {
			updateWidget(context, widgetId, item);
			PreferenceManager.put(context, PreferenceManager.KEY_LAST_INDEX + widgetId, index);
		}
	}

	protected void onClickPrev(Context context, int widgetId) {
		int index = PreferenceManager.getInt(context, PreferenceManager.KEY_LAST_INDEX + widgetId, 0);
		final RssItem item = DatabaseFactory.INSTANCE.getRssByWidgetId(Integer.toString(widgetId), --index);
		if (item != null) {
			updateWidget(context, widgetId, item);
			PreferenceManager.put(context, PreferenceManager.KEY_LAST_INDEX + widgetId, index);
		}
	}

	public static void runSchedule(Context context, int widgetId) {
		ScheduleManager.set(context, widgetId);
	}

	public static void updateWidget(Context context, int widgetId, RssItem rssItem) {
		if (rssItem == null) {
			return;
		}

		final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.rss_widget);

		remoteViews.setTextViewText(R.id.rss_widget_text_title, rssItem.getTitle());
		remoteViews.setTextViewText(R.id.rss_widget_text_description, rssItem.getDescription());

		remoteViews.setOnClickPendingIntent(R.id.rss_widget_button_next, getPendingSelfIntent(context, ACTION_CLICK_NEXT, widgetId));
		remoteViews.setOnClickPendingIntent(R.id.rss_widget_button_prev, getPendingSelfIntent(context, ACTION_CLICK_PREV, widgetId));

		AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews);
	}

	private void errorWidget(Context context, int widgetId) {
		final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.rss_widget);
		remoteViews.setTextViewText(R.id.rss_widget_text_title, context.getString(R.string.message_error_widget));
	}

	private static PendingIntent getPendingSelfIntent(Context context, String action, int widgetId) {
		final Intent intent = new Intent(context, RssWidgetProvider.class)
				.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}
