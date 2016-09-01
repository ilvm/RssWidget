package ilya.rsswidget.network;

import ilya.rsswidget.dto.RssItem;

import java.util.List;

public interface OnResponseListener {

	void onSuccess(List<RssItem> rssItemList, int widgetId, int startId);

	void onFail(Exception e, int widgetId, int startId);
}
