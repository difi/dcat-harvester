package no.difi.dcat.api.synd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;

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

	public DcatEntry() {
		
	}
	
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

	static DcatEntry getInstance(Resource r){

		DcatEntry dcatEntry = new DcatEntry();

		dcatEntry.keywords = new ArrayList<>();
		dcatEntry.formats = new ArrayList<>();

		dcatEntry.publisher = extractExactlyOneStringOrNull(r, DCTerms.publisher, FOAF.name);
		dcatEntry.subject = extractExactlyOneStringOrNull(r, DCTerms.title);

		StmtIterator keywordIterator = r.listProperties(ResourceFactory.createProperty("http://www.w3.org/ns/dcat#keyword"));

		while(keywordIterator.hasNext()){
			try{
				dcatEntry.keywords.add(keywordIterator.next().getString());
			}catch (JenaException e){
				e.printStackTrace();
			}
		}

		StmtIterator distributionIterator = r.listProperties(ResourceFactory.createProperty("http://www.w3.org/ns/dcat#distribution"));
		while(distributionIterator.hasNext()){
			try{
				Resource distribution = distributionIterator.next().getResource();
				String format = extractExactlyOneStringOrNull(distribution, DCTerms.format);
				if(format != null){
					dcatEntry.formats.add(format);
				}

			}catch (JenaException e){

			}
		}

		return dcatEntry;

	}

	private static String extractExactlyOneStringOrNull(Resource resource, Property ... p ) {

		for (int i = 0; i<p.length; i++) {


			StmtIterator stmtIterator = resource.listProperties(p[i]);
			if(i==p.length-1){
				try {
					return stmtIterator.next().getString();
				} catch (JenaException e) {
					return null;
				}
			}else{
				try {
					resource = stmtIterator.next().getResource();
				} catch (JenaException e) {
					return null;
				}
			}


		}


		return null;
	}




	
}
