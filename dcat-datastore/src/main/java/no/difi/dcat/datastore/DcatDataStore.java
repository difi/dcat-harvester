package no.difi.dcat.datastore;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
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

	public Model getDataCatalogue(String graphName) {
		logger.trace("Getting all catalogue in graph {}", graphName);
		Model model = fuseki.graph(graphName);
		return model;
	}
	
	public List<String> listGraphs() {
		logger.trace("Listing all graphs");
		String query = String.join("",
			"select distinct ?g where {",
				"graph ?g  {",
				"?a ?b ?c.",
				"}",
			"}"	);
		
		List<String> graphs = new ArrayList<>();
		
		ResultSet results = fuseki.select(query);
		while (results.hasNext()) {
			QuerySolution next = results.next();
			graphs.add(next.get("g").asResource().getURI());
		}
		return graphs;
		
	}
}
