package no.difi.dcat.datastore.domain.dcat;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class DCAT {
	
	private static final Model model = ModelFactory.createDefaultModel();
	
	public static final String NS = "http://www.w3.org/ns/dcat#";
	
	public static String getURI() {
		return NS;
	}
	
	public static final Resource NAMESPACE = model.createResource(NS);
	
	public static final Property dataset = model.createProperty(NS, "Dataset");
	
	public static final Property distribution = model.createProperty(NS, "Distribution");
	
	public static final Property catalog = model.createProperty(NS, "Catalog");

	public static final Property accessUrl = model.createProperty(NS, "accessURL");
	
	public static final Property landingPage = model.createProperty(NS, "landingPage");

	public static final Property keyword = model.createProperty(NS, "keyword");

	public static final Property contactPoint = model.createProperty(NS, "contactPoint");

}
