package no.difi.dcat.api.synd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
				DcatFeed dcatFeed = (DcatFeed) feedObj;
				Entry entry = new Entry();
				entry.setId(dcatFeed.getFeedId());
				entry.setPublished(dcatFeed.getPubDate());
				entry.setTitle(dcatFeed.getTitle());
				List<Link> links = new ArrayList<>();
				Link link = new Link();
				link.setHref(dcatFeed.getLink());
				links.add(link);
				entry.setAlternateLinks(links);
				Content content = new Content();
				content.setValue(dcatFeed.getDescription());
				entry.setSummary(content);
				DcatModule module = new DcatModule(new Date(), "Test", "123456789", "testing", Arrays.asList("test", "testing", "tests"), Arrays.asList("xml", "plaintext"));
				entry.getModules().add(module);
				entries.add(entry);
			}
		}
		return entries;
	}

}
