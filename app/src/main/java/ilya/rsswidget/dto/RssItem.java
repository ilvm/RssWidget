package ilya.rsswidget.dto;

import java.io.Serializable;

public class RssItem implements Serializable {

	private static final long serialVersionUID = 3852097649871994616L;

	private final String title, description;

	public RssItem(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RssItem item = (RssItem) o;

		if (title != null ? !title.equals(item.title) : item.title != null) return false;
		return description != null ? description.equals(item.description) : item.description == null;

	}

	@Override
	public int hashCode() {
		int result = title != null ? title.hashCode() : 0;
		result = 31 * result + (description != null ? description.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "RssItem{" +
				"title='" + title + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
