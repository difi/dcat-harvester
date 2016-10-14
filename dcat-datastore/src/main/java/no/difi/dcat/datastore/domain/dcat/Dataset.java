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
	private List<String> languages;
	private String landingPage;
	
	private String identifier;
	private String accessRights;
	private String frequency;
	private List<String> themes;
	private List<String> related;
	private List<String> spatial;
	
	private Temporal temporal;
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
	public List<String> getLanguages() {
		return languages;
	}
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
	public String getLandingPage() {
		return landingPage;
	}
	public void setLandingPage(String landingPage) {
		this.landingPage = landingPage;
	}
	public String getIdentifier() {
		return identifier;
	}
	public String getAccessRights() {
		return accessRights;
	}
	public String getFrequency() {
		return frequency;
	}
	public List<String> getThemes() {
		return themes;
	}
	public List<String> getRelated() {
		return related;
	}
	public List<String> getSpatial() {
		return spatial;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public void setAccessRights(String accessRights) {
		this.accessRights = accessRights;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public void setThemes(List<String> themes) {
		this.themes = themes;
	}
	public void setRelated(List<String> related) {
		this.related = related;
	}
	public void setSpatial(List<String> spatial) {
		this.spatial = spatial;
	}
	public Temporal getTemporal() {
		return temporal;
	}
	public void setTemporal(Temporal temporal) {
		this.temporal = temporal;
	}

	
	
	
}
