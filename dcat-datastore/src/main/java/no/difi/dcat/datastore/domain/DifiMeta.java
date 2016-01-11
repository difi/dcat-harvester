package no.difi.dcat.datastore.domain;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Created by havardottestad on 07/01/16.
 */
public class DifiMeta {

	public static final String DIFI_META = "http://dcat.difi.no/metadata/";

	public static final Property graph = ResourceFactory.createProperty(DIFI_META + "graph");
	public static final Property url = ResourceFactory.createProperty(DIFI_META + "url");
	public static final Property dcatSource = ResourceFactory.createProperty(DIFI_META + "dcatSource");
	public static Resource DcatSource = ResourceFactory.createResource(DIFI_META + "DcatSource");

}
