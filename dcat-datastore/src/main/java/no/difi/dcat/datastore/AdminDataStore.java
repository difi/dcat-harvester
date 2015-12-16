package no.difi.dcat.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminDataStore {
	
	private final Fuseki fuseki;
	private final Logger logger = LoggerFactory.getLogger(AdminDataStore.class);
	
	public AdminDataStore(Fuseki fuseki) {
		this.fuseki = fuseki;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<DcatSource> getDcatSources() {
		logger.trace("Listing all dcat sources");
		List<DcatSource> dcatSources = new ArrayList<DcatSource>();
		
		StringBuilder qb = new StringBuilder();
		qb.append("PREFIX difi: <http://dcat.difi.no/>\n");
		qb.append("SELECT ?name ?url ?user\n");
		qb.append("WHERE {\n");
		qb.append("?name difi:url ?url .\n");
		qb.append("OPTIONAL {?name difi:user ?user}\n");
		qb.append("} limit 100");

		ResultSet results = fuseki.select(qb.toString());
		
		while (results.hasNext()) {
			DcatSource dcatSource = DcatSource.fromQuerySolution(results.next());
			dcatSources.add(dcatSource);
		}
		
		return dcatSources;
				
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public List<DcatSource> getDcatSourcesForUser(String user) {
		logger.trace("Listing dcat sources for user {}", user);
		return getDcatSources().stream()
				.filter((DcatSource dcatSource) -> dcatSource.getUser().equalsIgnoreCase(user))
				.collect(Collectors.toList());
	}
	
	/**
	 * 
	 * @param dcatSource
	 */
	public void addDcatSource(DcatSource dcatSource) {
		logger.trace("Adding dcat source {}", dcatSource.getName());
		Model model = ModelFactory.createDefaultModel();
		
		model.add(model.createResource(dcatSource.getName()), model.createProperty("http://dcat.difi.no/url"), model.createResource(dcatSource.getUrl()));
		model.add(model.createResource(dcatSource.getName()), model .createProperty("http://dcat.difi.no/user"), model.createLiteral(dcatSource.getUser()));
		
		fuseki.drop(dcatSource.getName());
		fuseki.update(dcatSource.getName(), model);
	}
	
	/**
	 * 
	 * @param dcatSourceName
	 */
	public void deleteDcatSource(String dcatSourceName) {
		logger.trace("Deleting dcat source {}", dcatSourceName);
		fuseki.drop(dcatSourceName);
	}
}