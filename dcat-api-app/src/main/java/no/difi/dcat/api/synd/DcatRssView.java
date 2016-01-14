package no.difi.dcat.api.synd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Item;

public class DcatRssView extends AbstractRssFeedView {
	
	public DcatRssView() {
	}
	
	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel channel, 
			HttpServletRequest request) {
		channel.setTitle("Concretepage.com");
		channel.setLink("http://www.concretepage.com");
		channel.setDescription("Concretepage.com is a java tutorial.");
	}
	@Override
	protected List<Item> buildFeedItems(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<Item> items = new ArrayList<>();
	
		Object ob = model.get("feeds");
		if (ob instanceof List){
	           for(int i = 0; i < ((List<?>)ob).size(); i++){
	                Object feedObj = ((List<?>) ob).get(i);
	                DcatFeed dcatFeed = (DcatFeed)feedObj;
	    		Item item = new Item();
	    		item.setTitle(dcatFeed.getTitle());
	    		item.setLink(dcatFeed.getLink());
	    		item.setPubDate(dcatFeed.getPubDate());
	    		Content content = new Content();
	    		content.setValue(dcatFeed.getDescription());
	    		item.setContent(content);
				DcatModule module = new DcatModule(new Date(), "Test", "123456789", "testing", Arrays.asList("test", "testing", "tests"), Arrays.asList("xml", "plaintext"));
				item.getModules().add(module);
	    		items.add(item);
	           }
		}
		return items;
	}
}
