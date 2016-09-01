package ilya.rsswidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ilya.rsswidget.utils.PreferenceManager;

import java.util.regex.Pattern;

public class ConfigRssWidgetActivity extends Activity implements View.OnClickListener {

	private final Pattern PATTERN_URL = Pattern.compile("^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$");

	private EditText mEditTextLink;
	private int mWidgetId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config_rss_widget_activity);

		final Bundle extras = getIntent().getExtras();

		if (extras == null) {
			finish();
			return;
		}

		mWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		mEditTextLink = (EditText) findViewById(R.id.config_rss_widget_activity_edit_link);

		findViewById(R.id.config_rss_widget_activity_button_apply).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		final String rssLink = mEditTextLink.getText().toString().toLowerCase();
		if (!PATTERN_URL.matcher(rssLink).matches()) {
			Toast.makeText(ConfigRssWidgetActivity.this, R.string.message_error_input_url, Toast.LENGTH_SHORT).show();
			return;
		}

		PreferenceManager.put(ConfigRssWidgetActivity.this, PreferenceManager.KEY_RSS_LINK + mWidgetId, rssLink);
		RssWidgetProvider.runSchedule(ConfigRssWidgetActivity.this, mWidgetId);
		setResult(RESULT_OK, getIntent());
		finish();
	}
}
