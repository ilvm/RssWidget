package ilya.rsswidget.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import ilya.rsswidget.LoadService;

public class ScheduleManager {

	private static final long INTERVAL = 60000L;

	public static void set(Context context, int widgetId) {
		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		final long trigger = System.currentTimeMillis() - INTERVAL;
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, trigger, INTERVAL, getPendingIntent(context, widgetId));
	}

	public static void cancel(Context context, int widgetId) {
		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(getPendingIntent(context, widgetId));
	}

	private static PendingIntent getPendingIntent(Context context, int widgetId) {
		final Intent intent = new Intent(context, LoadService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		return PendingIntent.getService(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}
