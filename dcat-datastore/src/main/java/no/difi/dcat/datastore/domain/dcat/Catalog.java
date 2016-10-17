package no.difi.dcat.datastore.domain.dcat;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Catalog {

	private String id;
	private Map<String, String> title;
	private Map<String, String> description;
	private Publisher publisher;
	private Date issued;
	private Date modified;
	private Document homePage;
	private List<String> languages;
	private List<String> themeTaxonomies;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public void setLanguages(List<String> language) {
		this.languages = language;
	}
	public Document getHomePage() {
		return homePage;
	}
	public List<String> getThemeTaxonomies() {
		return themeTaxonomies;
	}
	public void setHomePage(Document homePage) {
		this.homePage = homePage;
	}
	public void setThemeTaxonomies(List<String> themeTaxonomies) {
		this.themeTaxonomies = themeTaxonomies;
	}
	
	
	
}
