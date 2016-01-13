package no.difi.dcat.api.synd;

import java.util.Date;
import java.util.List;

public class DcatEntry {

//	<datanorge:modified> : dc:modified
//	<datanorge:publisher> : dc:publisher -> foaf:name
//	<datanorge:orgnumber> : FINNES IKKE
//	<datanorge:subject> : dc:title
//	<datanorge:keyword> : dcat:keyword
//	<datanorge:format> : dcat:distribution -> dcat:mediaType / dct:format
	
	private Date modified;
	private String publisher;
	private String orgNumber;
	private String subject;
	private List<String> keywords;
	private List<String> formats;

	public DcatEntry(Date modified, String publisher, String orgNumber, String subject, List<String> keywords, List<String> formats) {
		this.modified = modified;
		this.publisher = publisher;
		this.orgNumber = orgNumber;
		this.subject = subject;
		this.keywords = keywords;
		this.formats = formats;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getOrgNumber() {
		return orgNumber;
	}

	public void setOrgNumber(String orgNumber) {
		this.orgNumber = orgNumber;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public List<String> getFormats() {
		return formats;
	}

	public void setFormats(List<String> formats) {
		this.formats = formats;
	}
	
}
