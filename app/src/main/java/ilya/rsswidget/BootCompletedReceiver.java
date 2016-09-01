package ilya.rsswidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		final AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
		final ComponentName provider = new ComponentName(context, RssWidgetProvider.class);

		final int[] widgetIds = widgetManager.getAppWidgetIds(provider);
		for (final int widgetId : widgetIds) {
			RssWidgetProvider.runSchedule(context, widgetId);
		}
	}
}
