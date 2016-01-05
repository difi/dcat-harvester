package no.difi.dcat.datastore;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DcatDataStore {
	
	private final Fuseki fuseki;
	private final Logger logger = LoggerFactory.getLogger(AdminDataStore.class);
	
	public DcatDataStore(Fuseki fuseki) {
		this.fuseki = fuseki;
	}
	
	/**
	 * Save a data catalogue, replacing existing data catalogues with the same name
	 * @param dcatName
	 * @param dcatModel
	 */
	public void saveDataCatalogue(String dcatName, Model dcatModel) {
		logger.trace("Adding data catalogue {}", dcatName);
		fuseki.drop(dcatName);
		fuseki.update(dcatName, dcatModel);
	}
	
	public Model getAllDataCatalogues() {
		logger.trace("Getting all data catalogues");
		Model model = fuseki.construct("CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o}");
		return model;
	}

}
