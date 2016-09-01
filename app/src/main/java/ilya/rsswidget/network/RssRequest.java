package ilya.rsswidget.network;

import android.os.AsyncTask;
import ilya.rsswidget.dto.RssItem;
import ilya.rsswidget.utils.RssHelper;
import org.xmlpull.v1.XmlPullParserException;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class RssRequest extends AsyncTask<String, Exception, List<RssItem>> {

	private final int mWidgetId, mStartId;
	private final OnResponseListener mListener;

	public RssRequest(int widgetId, int startId, OnResponseListener listener) {
		mWidgetId = widgetId;
		mStartId = startId;
		mListener = listener;
	}

	@Override
	protected void onProgressUpdate(Exception... exceptions) {
		super.onProgressUpdate(exceptions);
		if (mListener != null) {
			mListener.onFail(exceptions[0], mWidgetId, mStartId);
		}
	}

	@Override
	protected List<RssItem> doInBackground(String... urls) {

		List<RssItem> result = null;
		HttpsURLConnection connection = null;

		try {
			final URL url = new URL(urls[0]);
			connection = (HttpsURLConnection) url.openConnection();
			connection.connect();
			result = new RssHelper().parse(connection.getInputStream());
		} catch (IOException | XmlPullParserException e) {
			publishProgress(e);
			cancel(true);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(List<RssItem> rssItems) {
		if (mListener != null) {
			mListener.onSuccess(rssItems, mWidgetId, mStartId);
		}
	}
}
