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

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.module.ModuleImpl;

public class DcatModule extends ModuleImpl {

	public static final String URI = "http://data.norge.no";
	private static final long serialVersionUID = -2270589093650785086L;

	private Date modified;
	private String publisher;
	private String orgNumber;
	private String subject;
	private List<String> keywords;
	private List<String> formats;

	public DcatModule() {
		super(DcatModule.class, URI);
	}

	public DcatModule(Date modified, String publisher, String orgNumber, String subject, List<String> keywords,
			List<String> formats) {
		this();
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

	static DcatModule getInstance(Resource r) {

		DcatModule dcatModule = new DcatModule();

		dcatModule.keywords = new ArrayList<>();
		dcatModule.formats = new ArrayList<>();

		dcatModule.publisher = PropertyExtractor.extractExactlyOneStringOrNull(r, DCTerms.publisher, FOAF.name);
		dcatModule.subject = PropertyExtractor.extractExactlyOneStringOrNull(r, DCTerms.title);

		StmtIterator keywordIterator = r
				.listProperties(ResourceFactory.createProperty("http://www.w3.org/ns/dcat#keyword"));

		while (keywordIterator.hasNext()) {
			try {
				dcatModule.keywords.add(keywordIterator.next().getString());
			} catch (JenaException e) {
				e.printStackTrace();
			}
		}

		StmtIterator distributionIterator = r
				.listProperties(ResourceFactory.createProperty("http://www.w3.org/ns/dcat#distribution"));
		while (distributionIterator.hasNext()) {
			try {
				Resource distribution = distributionIterator.next().getResource();
				String format = PropertyExtractor.extractExactlyOneStringOrNull(distribution, DCTerms.format);
				if (format != null) {
					dcatModule.formats.add(format);
				}

			} catch (JenaException e) {

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
		setPublisher(module.getPublisher());
		setOrgNumber(module.getOrgNumber());
		setSubject(module.getSubject());
		setKeywords(module.getKeywords());
		setFormats(module.getKeywords());
	}

}
