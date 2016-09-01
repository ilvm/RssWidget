package ilya.rsswidget.utils;

import android.util.Xml;
import ilya.rsswidget.dto.RssItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RssHelper {

	private static final String NAME_SPACE = null;

	public List<RssItem> parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			final XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readRss(parser);
		} finally {
			in.close();
		}
	}

	private List<RssItem> readRss(XmlPullParser parser) throws XmlPullParserException, IOException {

		final List<RssItem> channels = new ArrayList<>();

		parser.require(XmlPullParser.START_TAG, NAME_SPACE, "rss");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			final String name = parser.getName();
			if (name.equals("channel")) {
				channels.addAll(readChannel(parser));
			} else {
				skip(parser);
			}
		}
		return channels;
	}

	private List<RssItem> readChannel(XmlPullParser parser) throws XmlPullParserException, IOException {

		final List<RssItem> items = new ArrayList<>();

		parser.require(XmlPullParser.START_TAG, NAME_SPACE, "channel");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			final String name = parser.getName();
			if (name.equals("item")) {
				items.add(readItem(parser));
			} else {
				skip(parser);
			}
		}
		return items;
	}

	private RssItem readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, NAME_SPACE, "item");
		String title = null;
		String description = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			final String name = parser.getName();
			switch (name) {
			case "title":
				title = readTitle(parser);
				break;
			case "description":
				description = readDescription(parser);
				break;
			default:
				skip(parser);
				break;
			}
		}
		return new RssItem(title, description);
	}

	private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, NAME_SPACE, "title");
		final String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, NAME_SPACE, "title");
		return title;
	}

	private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, NAME_SPACE, "description");
		final String summary = readText(parser);
		parser.require(XmlPullParser.END_TAG, NAME_SPACE, "description");
		return summary;
	}

	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		final String result;
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		} else {
			result = "";
		}
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}
}
