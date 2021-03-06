package no.difi.dcat.api.synd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.module.ModuleImpl;

import no.difi.dcat.datastore.domain.dcat.vocabulary.DCAT;

public class DcatModule extends ModuleImpl {

	public static final String URI = "http://data.norge.no";
	private static final long serialVersionUID = -2270589093650785086L;

	private Date modified;
	private Date issued;
	private String title;
	private String guid;
	private String description;
	private String publisher;
	private String orgNumber;
	private String accessRights;
	private String landingPage;
	private String distributionType;
	private List<String> subjects;
	private List<String> keywords;
	private List<String> formats;

	public DcatModule() {
		super(DcatModule.class, URI);
	}

	public DcatModule(Date modified, Date issued, String publisher, String orgNumber, String accessRight, List<String> subjects, List<String> keywords,
			List<String> formats, String title, String description, String landingPage, String identifier) {
		this();
		this.modified = modified;
		this.issued = issued;
		this.publisher = publisher;
		this.orgNumber = orgNumber;
		this.accessRights = accessRight;
		this.subjects = subjects;
		this.keywords = keywords;
		this.formats = formats;
		this.title = title;
		this.description = description;
		this.landingPage = landingPage;
		this.guid = identifier;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Date getIssued() {
		return issued;
	}

	public void setIssued(Date issued) {
		this.issued = issued;
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

	public String getAccessRights() {
		return accessRights;
	}

	public void setAccessRights(String accessRight) {
		this.accessRights = accessRight;
	}	
	
	public List<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
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

	public String getDistributionType() {
		return distributionType;
	}

	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title){
		this.title = title;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public String getLandingPage() {
		return landingPage;
	}

	public void setLandingPage(String landingPage) {
		this.landingPage = landingPage;
	}

	public String getGUID() {
		return guid;
	}

	public void setGUID(String guid) {
		this.guid = guid;
	}

	static DcatModule getInstance(Resource dataset) {

		DcatModule dcatModule = new DcatModule();

		dcatModule.subjects = new ArrayList<>();
		dcatModule.keywords = new ArrayList<>();
		dcatModule.formats = new ArrayList<>();

		// Dataset
		
		dcatModule.setPublisher(PropertyExtractor.extractExactlyOneStringOrNull(dataset, DCTerms.publisher, FOAF.name));
		dcatModule.setOrgNumber(PropertyExtractor.extractExactlyOneStringOrNull(dataset, DCTerms.publisher, DCTerms.identifier));
		
		dcatModule.setAccessRights(PropertyExtractor.extractExactlyOneStringOrNull(dataset, DCTerms.accessRights));
		dcatModule.setGUID(dataset.getURI());
		
		dcatModule.setTitle(PropertyExtractor.extractExactlyOneStringOrNull(dataset, DCTerms.title));
		dcatModule.setDescription(PropertyExtractor.extractExactlyOneStringOrNull(dataset, DCTerms.description));
		dcatModule.setLandingPage(PropertyExtractor.extractExactlyOneStringOrNull(dataset, DCAT.landingPage));
		
		StmtIterator keywordIterator = dataset
				.listProperties(DCAT.keyword);
		while (keywordIterator.hasNext()) {
			try {
				dcatModule.getKeywords().add(keywordIterator.next().getString());
			} catch (JenaException e) {
				e.printStackTrace();
			}
		}
		
		StmtIterator subjectIterator = dataset
				.listProperties(DCAT.theme);
		while (subjectIterator.hasNext()) {
			try {
				Statement next = subjectIterator.next();
				String subject = null;
				if (next.getObject().isLiteral()) {
					subject = next.getString();
				} else {
					subject = next.getObject().asResource().getURI();
				}
				dcatModule.getSubjects().add(subject);
			} catch (JenaException e) {
				e.printStackTrace();
			}
		}

		String modified = PropertyExtractor.extractExactlyOneStringOrNull(dataset, DCTerms.modified);
		if (modified != null && !modified.equals("")) {
			try{
				dcatModule.setModified(DatatypeConverter.parseDate(modified).getTime());
			}catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		
		String issued = PropertyExtractor.extractExactlyOneStringOrNull(dataset, DCTerms.issued);
		if (issued != null && !issued.equals("")) {
			try{
				dcatModule.setIssued(DatatypeConverter.parseDate(issued).getTime());
			}catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		} 
		
		// Distribution
		
		StmtIterator stmtIterator = dataset.listProperties(DCAT.distribution);
		while (stmtIterator.hasNext()) {
			Statement next = stmtIterator.next();
			if (next.getObject().isResource()) {
				String format = PropertyExtractor.extractExactlyOneStringOrNull(next.getResource(), DCTerms.format);
				if (format != null) {
					if (!dcatModule.getFormats().contains(format)) {
						dcatModule.getFormats().add(format);
					}
				}
				dcatModule.setDistributionType(PropertyExtractor.extractExactlyOneStringOrNull(next.getResource(), DCTerms.type));
			}
		}
		

		return dcatModule;
	}

	@Override
	public Class<? extends CopyFrom> getInterface() {
		return DcatModule.class;
	}

	@Override
	public void copyFrom(CopyFrom obj) {
		DcatModule module = (DcatModule) obj;
		setModified(module.getModified());
		setIssued(module.getIssued());
		setPublisher(module.getPublisher());
		setOrgNumber(module.getOrgNumber());
		setAccessRights(module.getAccessRights());
		setSubjects(module.getSubjects());
		setKeywords(module.getKeywords());
		setFormats(module.getKeywords());
		setTitle(module.getTitle());
		setDescription(module.getDescription());
		setGUID(module.getGUID());
	}

	




}
