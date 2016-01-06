package no.difi.dcat.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.query.QuerySolution;
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
		qb.append("SELECT ?name ?url ?user ?description\n");
		qb.append("WHERE {\n");
		qb.append("?name difi:url ?url .\n");
		qb.append("?name difi:description ?description .\n");
		qb.append("?name difi:user ?user\n");
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
	 * @param dcatSourceName
	 * @return
	 */
	public Optional<DcatSource> getDcatSourceByName(String dcatSourceName) {
		logger.trace("Getting dcat source by name {}", dcatSourceName);
		return getDcatSources().stream()
				.filter((DcatSource dcatSource) -> dcatSource.getName().equalsIgnoreCase(dcatSourceName))
				.findFirst();
	}
	
	/**
	 * 
	 * @param dcatSource
	 */
	public void addDcatSource(DcatSource dcatSource) {
		logger.trace("Adding dcat source {}", dcatSource.getName());
		Model model = ModelFactory.createDefaultModel();
		
		model.add(model.createResource(dcatSource.getName()), model.createProperty("http://dcat.difi.no/description"), model.createLiteral(dcatSource.getDescription()));
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
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @param role
	 */
	public void addUser(String username, String password, String role) {
		logger.trace("Adding user {}", username);
		Model model = ModelFactory.createDefaultModel();
		
		String user = String.format("http://dcat.difi.no/%s", username);
		
		model.add(model.createResource(user), model.createProperty("http://dcat.difi.no/password"), model.createLiteral(password));
		model.add(model.createResource(user), model.createProperty("http://dcat.difi.no/role"), model.createLiteral(role.toUpperCase()));
		
		fuseki.drop(user);
		fuseki.update(user, model);
	}
	
	/**
	 * 
	 * @param username
	 */
	public void deleteUser(String username) {
		logger.trace("Deleting user {}", username);
		String user = String.format("http://dcat.difi.no/%s", username);
		fuseki.drop(user);
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public Map<String,String> getUser(String username) {
		logger.trace("Getting user {}", username);
		String user = String.format("<http://dcat.difi.no/%s>", username);
		
		StringBuilder qb = new StringBuilder();
		qb.append("PREFIX difi: <http://dcat.difi.no/>\n");
		qb.append("SELECT ?password ?role\n");
		qb.append("WHERE {\n");
		qb.append(user+ " difi:password ?password .\n");
		qb.append(user+" difi:role ?role\n");
		qb.append("} limit 1");

		ResultSet results = fuseki.select(qb.toString());
		
		Map<String, String> userMap = new HashMap<>();
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			userMap.put("username", username);
			userMap.put("password", qs.get("password").asLiteral().toString());
			userMap.put("role", qs.get("role").asLiteral().toString());
		}
		
		return userMap;
		
	}
}