package no.difi.dcat.harvester.crawler;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.DcatSource;
import no.difi.dcat.datastore.Fuseki;

public class CrawlerResultHandler {

	private final DcatDataStore dcatDataStore;
	
	private final Logger logger = LoggerFactory.getLogger(CrawlerResultHandler.class);
	
	public CrawlerResultHandler(String serviceUri) {
		this.dcatDataStore = new DcatDataStore(new Fuseki(serviceUri));
	}

	public void process(DcatSource dcatSource, Model model) {
		logger.trace("Processing results from crawling");
		dcatDataStore.saveDataCatalogue(dcatSource.getName(), model);
	}

}
