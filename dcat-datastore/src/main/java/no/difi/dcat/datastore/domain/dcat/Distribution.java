package no.difi.dcat.datastore.domain.dcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Distribution {
	
	private String id;
	private Map<String,String> title;
	private Map<String,String> description;
	private String accessURL;
	private String license;
	private List<String> format;
	private List<String> pages;
	private List<String> conformsTo;
	private List<String> downloadURL;
	
	private Dataset dataset;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Dataset getDataset() {
		return dataset;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	public Map<String, String> getTitle() {
		if (title == null) {
			title = new HashMap<>();
		}
		return title;
	}
	public void setTitle(Map<String, String> title) {
		this.title = title;
	}
	public Map<String, String> getDescription() {
		if (description == null) {
			description = new HashMap<>();
		}
		return description;
	}
	public void setDescription(Map<String, String> description) {
		this.description = description;
	}
	public String getAccessURL() {
		return accessURL;
	}
	public void setAccessURL(String accessURL) {
		this.accessURL = accessURL;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public List<String> getFormat() {
		if (format == null) {
			format = new ArrayList<String>();
		}
		return format;
	}
	public void setFormat(List<String> format) {
		this.format = format;
	}
	public List<String> getPages() {
		if (pages == null) {
			pages = new ArrayList<String>();
		}
		return pages;
	}
	public void setPages(List<String> pages) {
		this.pages = pages;
	}
	public List<String> getConformsTo() {
		if (conformsTo == null) {
			conformsTo = new ArrayList<String>();
		}
		return conformsTo;
	}
	public void setConformsTo(List<String> conformsTo) {
		this.conformsTo = conformsTo;
	}
	public List<String> getDownloadURL() {
		if (downloadURL == null) {
			downloadURL = new ArrayList<String>();
		}
		return downloadURL;
	}
	public void setDownloadURL(List<String> downloadURL) {
		this.downloadURL = downloadURL;
	}
	
	
	
	
}