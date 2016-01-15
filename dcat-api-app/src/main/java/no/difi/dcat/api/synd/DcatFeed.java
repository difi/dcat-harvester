package no.difi.dcat.api.synd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Repository;

@Repository
public class DcatFeed {
	
	private String feedId;
	private String title;
	private String description;
	private Date pubDate;
	private String link;
	private DcatModule dcatModule;

	public static final String DCAT_NAMESPACE = "http://www.w3.org/ns/dcat#";
	
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
	
	public static DcatFeed getInstance(Resource dataset, Resource distribution) {
		DcatFeed dcatFeed = new DcatFeed();
		
		DcatModule dcatModule = DcatModule.getInstance(dataset, distribution);
		dcatFeed.setDcatModule(dcatModule);
		
		dcatFeed.setFeedId(PropertyExtractor.extractExactlyOneStringOrNull(dataset, DCTerms.identifier));
		dcatFeed.setTitle(PropertyExtractor.extractExactlyOneStringOrNull(dataset, DCTerms.title));
		dcatFeed.setDescription(PropertyExtractor.extractExactlyOneStringOrNull(dataset, DCTerms.description));
		dcatFeed.setLink(PropertyExtractor.extractExactlyOneStringOrNull(distribution, ResourceFactory.createProperty(DCAT_NAMESPACE, "accessURL")));
		String pubDate = PropertyExtractor.extractExactlyOneStringOrNull(dataset, DCTerms.issued);
		if (pubDate != null) {
			dcatFeed.setPubDate(DatatypeConverter.parseDate(pubDate).getTime());
		}
		
		return dcatFeed;
	}
	
	public List<DcatFeed> createFeed(Model model) {
		List<DcatFeed> feeds = new ArrayList<>();
		
		ResIterator datasets = model.listResourcesWithProperty(RDF.type, ResourceFactory.createProperty(DCAT_NAMESPACE, "Dataset"));
		while (datasets.hasNext()) {
			Resource dataset = datasets.next();
			ResIterator distributions = model.listResourcesWithProperty(RDF.type, ResourceFactory.createProperty(DCAT_NAMESPACE, "Distribution"));
			while (distributions.hasNext()) {
				Resource distribution = distributions.next();
				DcatFeed dcatFeed = DcatFeed.getInstance(dataset, distribution);
				feeds.add(dcatFeed);
			}
		}
		return feeds;
	}
}