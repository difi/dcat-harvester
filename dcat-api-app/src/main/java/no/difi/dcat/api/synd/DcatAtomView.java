package no.difi.dcat.api.synd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;

import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;

public class DcatAtomView extends AbstractAtomFeedView {

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Feed feed, HttpServletRequest request) {
		feed.setId("id1234");
		feed.setTitle("Concretepage.com");
		List<Link> links = new ArrayList<>();
		Link link = new Link();
		link.setHref("http://www.concretepage.com");
		links.add(link);
		feed.setAlternateLinks(links);
	}

	@Override
	protected List<Entry> buildFeedEntries(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Entry> entries = new ArrayList<Entry>();
		Object ob = model.get("feeds");
		if (ob instanceof List) {
			for (int i = 0; i < ((List<?>) ob).size(); i++) {
				Object feedObj = ((List<?>) ob).get(i);
				DcatFeed DcatFeed = (DcatFeed) feedObj;
				Entry entry = new Entry();
				entry.setId(DcatFeed.getFeedId());
				entry.setPublished(DcatFeed.getPubDate());
				entry.setTitle(DcatFeed.getTitle());
				List<Link> links = new ArrayList<>();
				Link link = new Link();
				link.setHref(DcatFeed.getLink());
				links.add(link);
				entry.setAlternateLinks(links);
				Content content = new Content();
				content.setValue(DcatFeed.getDescription());
				entry.setSummary(content);
				entries.add(entry);
			}
		}
		return entries;
	}

}
