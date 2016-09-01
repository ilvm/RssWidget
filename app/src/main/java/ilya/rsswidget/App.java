package ilya.rsswidget;

import android.app.Application;
import ilya.rsswidget.database.DatabaseFactory;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		DatabaseFactory.INSTANCE.init(this);
	}
}
