package no.difi.dcat.datastore;

import org.apache.jena.rdf.model.Model;

public class DcatDataStore {
	
	private final Fuseki fuseki;
	
	public DcatDataStore(Fuseki fuseki) {
		this.fuseki = fuseki;
	}
	
	/**
	 * Save a data catalogue, replacing existing data catalogues with the same name
	 * @param dcatName
	 * @param dcatModel
	 */
	public void saveDataCatalogue(String dcatName, Model dcatModel) {
		fuseki.drop(dcatName);
		fuseki.update(dcatName, dcatModel);
	}

}
