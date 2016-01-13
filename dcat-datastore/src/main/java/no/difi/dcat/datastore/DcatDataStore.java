package no.difi.dcat.datastore;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.domain.DcatSource;

public class DcatDataStore {

	private final Fuseki fuseki;
	private final Logger logger = LoggerFactory.getLogger(AdminDataStore.class);

	public DcatDataStore(Fuseki fuseki) {
		this.fuseki = fuseki;
	}

	/**
	 * Save a data catalogue, replacing existing data catalogues with the same name
	 * @param dcatSource
	 * @param dcatModel
	 */
	public void saveDataCatalogue(DcatSource dcatSource, Model dcatModel) {
		logger.info("Adding data catalogue {}", dcatSource.getGraph());
		fuseki.drop(dcatSource.getGraph());
		fuseki.update(dcatSource.getGraph(), dcatModel);
	}

	public Model getAllDataCatalogues() {
		logger.trace("Getting all data catalogues");
		Model model = fuseki.graph();
		return model;
	}

	public void deleteDataCatalogue(DcatSource dcatSource) {
		if(dcatSource == null || dcatSource.getGraph() == null || dcatSource.getGraph().trim().equals("")) return;
		fuseki.drop(dcatSource.getGraph());
	}
}
