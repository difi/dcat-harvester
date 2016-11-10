package no.difi.dcat.datastore.domain.dcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Distribution implements Cloneable{

	private String id;
	private String dcatSourceId;
	private Map<String,String> title;
	private Map<String,String> description;
	private String accessURL;
	private String license;
	private String distributionType;
	private List<String> format;
	private List<Document> pages;
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
	public String getDistributionType() {
		return distributionType;
	}
	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
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
	public List<Document> getPages() {
		if (pages == null) {
			pages = new ArrayList<Document>();
		}
		return pages;
	}
	public void setPages(List<Document> pages) {
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


	public String getDcatSourceId() {
		return dcatSourceId;
	}
	public void setDcatSourceId(String dcatSourceId) {
		this.dcatSourceId = dcatSourceId;
	}
	@Override
	public Distribution clone(){
		Distribution clone = new Distribution();

		clone.setAccessURL(accessURL);
		clone.setConformsTo(conformsTo);
		clone.setDataset(dataset);
		clone.setDescription(description);
		clone.setDownloadURL(downloadURL);
		clone.setDistributionType(distributionType);
		clone.setFormat(format);
		clone.setId(id);
		clone.setLicense(license);
		clone.setPages(pages);
		clone.setTitle(title);

		return clone;
	}
	public static Distribution[] splitFormat(Distribution distribution) {
		if (distribution.getFormat().size() == 0) {
			return new Distribution[] {distribution};
		}else{
			Distribution[] distributions = new Distribution[distribution.getFormat().size()];
			for (int i = 0; i < distributions.length; i++) {
				distributions[i] = distribution.clone();

				distributions[i].setId(distributions[i].getId() + "/" + i);

				List<String> format = new ArrayList<String>();
				format.add(distribution.getFormat().get(i));
				distributions[i].setFormat(format);
			}

			return distributions;
		}
	}
}





