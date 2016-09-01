package ilya.rsswidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import ilya.rsswidget.database.DatabaseFactory;
import ilya.rsswidget.dto.RssItem;
import ilya.rsswidget.network.OnResponseListener;
import ilya.rsswidget.network.RssRequest;
import ilya.rsswidget.utils.PreferenceManager;

import java.util.List;

public class LoadService extends Service implements OnResponseListener {

	private static final String TAG_WAKE_LOCK_RSS_WIDGET = "tag_wake_lock_rss_widget";
	private PowerManager.WakeLock mWakeLock;

	@Override
	public IBinder onBind(Intent intent) {
		throw new RuntimeException("Do not go here");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mWakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
				.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG_WAKE_LOCK_RSS_WIDGET);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mWakeLock.acquire();
		final int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		RssRequest rssRequest = new RssRequest(widgetId, startId, this);
		rssRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
				PreferenceManager.getString(this, PreferenceManager.KEY_RSS_LINK + widgetId, null));
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onSuccess(List<RssItem> rssItemList, int widgetId, int startId) {
		final boolean isEquals = rssItemList.equals(DatabaseFactory.INSTANCE.getRssLisByWidgetId(Integer.toString(widgetId)));
		if (!isEquals) {
			DatabaseFactory.INSTANCE.insertRss(Integer.toString(widgetId), rssItemList);
			final Intent intent = new Intent(RssWidgetProvider.ACTION_RESPONSE_SUCCESS);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			intent.putExtra(RssWidgetProvider.KEY_EXTRA_RSS_ITEM, rssItemList.get(0));
			sendBroadcast(intent);
		}
		stopSelf(startId);
	}

	@Override
	public void onFail(Exception e, int widgetId, int startId) {
		sendBroadcast(new Intent(RssWidgetProvider.ACTION_RESPONSE_FAIL)
				.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId));
		stopSelf(startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mWakeLock.release();
	}
}
