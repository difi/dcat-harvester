package no.difi.dcat.harvester.crawler;

import no.difi.dcat.harvester.validation.DcatValidation;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.DcatSource;
import no.difi.dcat.datastore.Fuseki;

public class CrawlerResultHandler {

	private final DcatDataStore dcatDataStore;
	
	private final Logger logger = LoggerFactory.getLogger(CrawlerResultHandler.class);
	
	public CrawlerResultHandler(DcatDataStore dcatDataStore) {
		this.dcatDataStore = dcatDataStore;
	}
	
	public CrawlerResultHandler(String serviceUri) {
		this.dcatDataStore = new DcatDataStore(new Fuseki(serviceUri));
	}



	public void process(DcatSource dcatSource, Model model) {
		logger.trace("Starting processing of results");



		if(DcatValidation.validate(model, (error)->{
			if(error.isError()){
				logger.error(error.toString());
			}if(error.isWarning()){
				logger.warn(error.toString());
			}else {
				logger.info(error.toString());
			}
		})){
			dcatDataStore.saveDataCatalogue(dcatSource.getId(), model);
		}
		logger.trace("Finished processing of results");
	}



}





