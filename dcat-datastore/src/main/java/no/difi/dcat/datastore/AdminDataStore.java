package no.difi.dcat.datastore;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class AdminDataStore {

	private final Fuseki fuseki;
	private final Logger logger = LoggerFactory.getLogger(AdminDataStore.class);

	public AdminDataStore(Fuseki fuseki) {
		this.fuseki = fuseki;
	}

	/**
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
	 * @param user
	 * @return
	 */
	public List<DcatSource> getDcatSourcesForUser(String user) {
		//@TODO Use SPARQL update instead

		logger.trace("Listing dcat sources for user {}", user);
		return getDcatSources().stream()
				.filter((DcatSource dcatSource) -> dcatSource.getUser().equalsIgnoreCase(user))
				.collect(Collectors.toList());
	}

	/**
	 * @param dcatSourceName
	 * @return
	 */
	public Optional<DcatSource> getDcatSourceByName(String dcatSourceName) {
		//@TODO Use SPARQL update instead

		logger.trace("Getting dcat source by name {}", dcatSourceName);
		return getDcatSources().stream()
				.filter((DcatSource dcatSource) -> dcatSource.getId().equalsIgnoreCase(dcatSourceName))
				.findFirst();
	}

	/**
	 * @param dcatSource
	 */
	public URI addDcatSource(DcatSource dcatSource) {
		//@TODO Use SPARQL update instead

		logger.trace("Adding dcat source {}", dcatSource.getId());

		String dcatSourceUri = "http://dcat.difi.no/dcatSource_" + UUID.randomUUID().toString();
		String dcatGraphUri = "http://dcat.difi.no/dcatSource_" + UUID.randomUUID().toString();


		String query = String.join("\n",
				"insert {",
				"     graph <http://dcat.difi.no/usersGraph/> {",
				"          ?user difiMeta:dcatSource ?dcatSource .",
				"          ?dcatSource a difiMeta:DcatSource ; ",
				"                             difiMeta:graph ?dcatGraphUri; ",
				"                             difiMeta:url ?url;",
				"",
				"",
				" . ",
				"     }",
				"} where {",
				"           BIND(IRI(?dcatSourceUri) as ?dcatSource)",
				"           ?user foaf:accountName ?username",
				"}");


		Map<String, String> map = new HashMap<>();

		map.put("username", dcatSource.getUser());
		map.put("dcatSourceUri", dcatSourceUri);
		map.put("dcatGraphUri", dcatGraphUri);
		map.put("url", dcatSource.getUrl());


		fuseki.sparqlUpdate(query, map);

		if (fuseki.ask("ask { ?a ?b <" + dcatSourceUri + ">}")) {
			return URI.create(dcatSourceUri);
		} else {
			//@TODO throw exception?
			return null;
		}

//
//            Model model = ModelFactory.createDefaultModel();
//
//            model.add(model.createResource(dcatSource.getId()), model.createProperty("http://dcat.difi.no/description"), model.createLiteral(dcatSource.getDescription()));
//            model.add(model.createResource(dcatSource.getId()), model.createProperty("http://dcat.difi.no/url"), model.createResource(dcatSource.getUrl()));
//            model.add(model.createResource(dcatSource.getId()), model.createProperty("http://dcat.difi.no/user"), model.createLiteral(dcatSource.getUser()));
//
//            fuseki.drop(dcatSource.getId());
//            fuseki.update(dcatSource.getId(), model);
	}

	/**
	 * @param dcatSourceName
	 */
	public void deleteDcatSource(String dcatSourceName) {
		logger.trace("Deleting dcat source {}", dcatSourceName);
		fuseki.drop(dcatSourceName);
	}

	/**
	 * @param username
	 * @param password
	 * @param role
	 */
	public URI addUser(String username, String password, String role) throws UserAlreadyExistsException {
		logger.trace("Adding user {}", username);


		String user = "http://dcat.difi.no/user_" + UUID.randomUUID().toString();

		String query = String.join("\n",
				"insert {",
				"     graph <http://dcat.difi.no/usersGraph/> {",
				"           <" + user + "> foaf:accountName ?username;",
				"                               difiMeta:role ?role;",
				"                               difiMeta:password ?password;" +
						"                               a difiMeta:User;",
				"            .",
				"     }",
				"} where {",
				"     FILTER(!EXISTS{?a foaf:accountName ?username})",
				"}"
		);


		Map<String, String> map = new HashMap<>();
		map.put("username", username);
		map.put("role", role);
		map.put("password", password);


		fuseki.sparqlUpdate(query, map);

		if (fuseki.ask("ask { <" + user + "> ?b ?c}")) {
			return URI.create(user);
		} else {
			throw new UserAlreadyExistsException(username);
		}

	}

	/**
	 * @param username
	 */
	public void deleteUser(String username) {
		//@TODO Use SPARQL update instead
		logger.trace("Deleting user {}", username);
		String user = String.format("http://dcat.difi.no/%s", username);
		fuseki.drop(user);
	}

	/**
	 * @param username
	 * @return
	 */
	public Map<String, String> getUser(String username) {
		logger.trace("Getting user {}", username);


		Map<String, String> map = new HashMap<>();
		map.put("username", username);
		String query = String.join("\n",
				"select ?password ?role where {",
				"     ?user foaf:accountName ?username ;",
				"           difiMeta:password ?password ;",
				"           difiMeta:role ?role ;",
				".",
				"}"
		);
		ResultSet results = fuseki.select(query, map);

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