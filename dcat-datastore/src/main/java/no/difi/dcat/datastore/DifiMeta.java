package no.difi.dcat.datastore;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Created by havardottestad on 07/01/16.
 */
public class DifiMeta {

	static final String DIFI_META = "http://dcat.difi.no/metadata/";

	static final Property graph = ResourceFactory.createProperty(DIFI_META + "graph");
	static final Property url = ResourceFactory.createProperty(DIFI_META + "url");
	static final Property dcatSource = ResourceFactory.createProperty(DIFI_META + "dcatSource");


}
