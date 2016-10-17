package no.difi.dcat.api.synd;

import java.text.DateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.jdom2.Element;
import org.jdom2.Namespace;

import com.rometools.rome.feed.module.Module;
import com.rometools.rome.io.ModuleGenerator;

import no.difi.dcat.datastore.domain.dcat.vocabulary.DCAT;

public class DcatModuleGenerator implements ModuleGenerator {

	private static final Namespace NAMESPACE = Namespace.getNamespace("datanorge", DcatModule.URI);
	private static final Namespace NSFOAF = Namespace.getNamespace("foaf", FOAF.NS);
	private static final Namespace NSDCT = Namespace.getNamespace("dct", DCTerms.NS);
	private static final Namespace NSDCAT = Namespace.getNamespace("dcat", DCAT.NS);
	private static final Set<Namespace> NAMESPACES;

	static {
		Set<Namespace> namespaces = new HashSet<Namespace>();
		namespaces.add(NAMESPACE);
		namespaces.add(NSFOAF);
		namespaces.add(NSDCT);
		namespaces.add(NSDCAT);
		NAMESPACES = Collections.unmodifiableSet(namespaces);
	}

	@Override
	public String getNamespaceUri() {
		return DcatModule.URI;
	}

	@Override
	public Set<Namespace> getNamespaces() {
		return NAMESPACES;
	}

	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	@Override
	public void generate(Module module, Element element) {
		DcatModule dcatModule = (DcatModule) module;
		if (dcatModule.getTitle() != null) {
			Element el = new Element("title", NSDCT);
			el.setText(dcatModule.getTitle());
			element.addContent(el);
		}
		if (dcatModule.getDescription() != null) {
			Element el = new Element("description", NSDCT);
			el.setText(dcatModule.getDescription());
			element.addContent(el);
		}
		if (dcatModule.getLandingPage() != null) {
			Element el = new Element("landingPage", NSDCAT);
			el.setText(dcatModule.getLandingPage());
			element.addContent(el);
		}
		if (dcatModule.getModified() != null) {
			Element el = new Element("modified", NSDCT);
			el.setText(df.format(dcatModule.getModified()));
			element.addContent(el);
		}
		if (dcatModule.getPublisher() != null) {
			Element el = new Element("publisher", NSDCT);
			el.setText(dcatModule.getPublisher());
			element.addContent(el);
		}
		if (dcatModule.getOrgNumber() != null) {
			Element el = new Element("orgnumber", NAMESPACE);
			el.setText(dcatModule.getOrgNumber());
			element.addContent(el);
		}
		if (dcatModule.getAccessRight() != null) {
			Element el = new Element("accessRight", NSDCT);
			el.setText(dcatModule.getAccessRight());
			element.addContent(el);
		}
		if (dcatModule.getSubjects() != null) {
			for(String subject: dcatModule.getSubjects()) {
				Element el = new Element("theme", NSDCAT);
				el.setText(subject);
				element.addContent(el);
			}
		}
		if (dcatModule.getKeywords() != null) {
			for (String keyword : dcatModule.getKeywords()) {
				Element el = new Element("keyword", NSDCAT);
				el.setText(keyword);
				element.addContent(el);
			}
		}		
		if (dcatModule.getFormats() != null) {
			for (String format : dcatModule.getFormats()) {
				Element el = new Element("format", NSDCT);
				el.setText(format);
				element.addContent(el);
			}
		}
	}

}
