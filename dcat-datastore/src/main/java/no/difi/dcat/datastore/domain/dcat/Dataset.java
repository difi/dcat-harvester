package no.difi.dcat.datastore.domain.dcat;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Dataset {
	
	private String id;
	private Map<String,String> title;
	private Map<String,String> description;
	private Contact contact;
	private Map<String, List<String>> keywords;
	private Publisher publisher;
	private Date issued;
	private Date modified;
	private String language;
	private String landingPage;
	
	private Catalog catalog;
	private List<Distribution> distributions;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Distribution> getDistributions() {
		return distributions;
	}
	public void setDistributions(List<Distribution> distributions) {
		this.distributions = distributions;
	}
	public Catalog getCatalog() {
		return catalog;
	}
	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}
	public Map<String, String> getTitle() {
		return title;
	}
	public void setTitle(Map<String, String> title) {
		this.title = title;
	}
	public Map<String, String> getDescription() {
		return description;
	}
	public void setDescription(Map<String, String> description) {
		this.description = description;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public Map<String, List<String>> getKeywords() {
		return keywords;
	}
	public void setKeywords(Map<String, List<String>> keywords) {
		this.keywords = keywords;
	}
	public Publisher getPublisher() {
		return publisher;
	}
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}
	public Date getIssued() {
		return issued;
	}
	public void setIssued(Date issued) {
		this.issued = issued;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getLandingPage() {
		return landingPage;
	}
	public void setLandingPage(String landingPage) {
		this.landingPage = landingPage;
	}

	
	
}
