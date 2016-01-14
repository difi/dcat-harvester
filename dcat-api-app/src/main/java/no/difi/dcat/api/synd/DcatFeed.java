package no.difi.dcat.api.synd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class DcatFeed {
	
	private String feedId;
	private String title;
	private String description;
	private Date pubDate;
	private String link;
	private DcatModule dcatModule;

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public DcatModule getDcatModule() {
		return dcatModule;
	}

	public void setDcatModule(DcatModule dcatModule) {
		this.dcatModule = dcatModule;
	}
	
	public List<DcatFeed> createFeed(Model model) {
		List<DcatFeed> feeds = new ArrayList<>();
		
		ResIterator iterator = model.listResourcesWithProperty(model.createProperty("http://www.w3.org/ns/dcat#", "accessURL"));
		while (iterator.hasNext()) {
			Resource next = iterator.next();
			DcatModule dcatModule = DcatModule.getInstance(next);
			
			DcatFeed dcatFeed = new DcatFeed();
			dcatFeed.setFeedId("100");
			dcatFeed.setTitle("Title one");
			dcatFeed.setDescription("This is description one");
			dcatFeed.setLink("http://www.urlone.com");
			dcatFeed.setPubDate(new Date());
			dcatFeed.setDcatModule(dcatModule);
			feeds.add(dcatFeed);
		}
		
		return feeds;
	}
}