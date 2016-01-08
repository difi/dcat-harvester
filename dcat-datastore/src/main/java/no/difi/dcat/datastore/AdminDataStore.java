package no.difi.dcat.datastore;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;

public class AdminDataStore {

	protected final Fuseki fuseki;
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
	 * @param username
	 * @return
	 */
	public List<DcatSource> getDcatSourcesForUser(String username) {
		logger.trace("Listing dcat sources for user {}", username);


		Map<String, String> map = new HashMap<>();
		map.put("username", username);

		String query = String.join("\n",
				"describe ?a ?user where {",
				"	?user foaf:accountName ?username;",
				"	difiMeta:dcatSource ?a.",
				"}"
		);
		Model dcatModel = fuseki.describe(query, map);

		if (dcatModel.isEmpty()) {
			return new ArrayList<>();
		}

		StmtIterator stmtIterator = dcatModel
				.listResourcesWithProperty(FOAF.accountName).next()
				.listProperties(DifiMeta.dcatSource);

		List<DcatSource> dcatSources = new ArrayList<>();

		while (stmtIterator.hasNext()) {
			dcatSources.add(new DcatSource(dcatModel, stmtIterator.nextStatement().getResource().toString()));
		}

		return dcatSources;

	}

	/**
	 * @param dcatSourceId
	 * @return
	 */
	public Optional<DcatSource> getDcatSourceById(String dcatSourceId) {

		logger.trace("Getting dcat source by id {}", dcatSourceId);

		Map<String, String> map = new HashMap<>();
		map.put("dcatSourceId", dcatSourceId);

		String query = String.join("\n",
				"describe ?a ?user where {",
				"	BIND(IRI(?dcatSourceId) as ?a)",
				"	?user difiMeta:dcatSource ?a.",
				"}"
		);
		Model dcatModel = fuseki.describe(query, map);

		System.out.println(DifiMeta.DcatSource);

		if (!dcatModel.listResourcesWithProperty(RDF.type, DifiMeta.DcatSource).hasNext()) {
			System.out.println("EMPTY");
			return Optional.empty();
		}

		System.out.println(new DcatSource(dcatModel, dcatSourceId));

		return Optional.of(new DcatSource(dcatModel, dcatSourceId));


	}

	/**
	 * @param dcatSource
	 */
	public DcatSource addDcatSource(DcatSource dcatSource) {

		logger.trace("Adding dcat source {}", dcatSource.getId());

		if (dcatSource.getId() != null && dcatSource.getGraph() == null) {
			//get current graph
			Optional<DcatSource> dcatSourceById = getDcatSourceById(dcatSource.getId());
			if (dcatSourceById.isPresent()) {
				dcatSource.setGraph(dcatSourceById.get().getGraph());
			}
		}

		if (dcatSource.getId() == null && dcatSource.getGraph() != null) {
			throw new UnsupportedOperationException("dcatSource id can not  ==null while the graph is != null. This is potentially dangerous behaviour.");
		}

		if (dcatSource.getUser() == null) {
			throw new UnsupportedOperationException("Not allowed to add a dcatSource without a user");

		}

		if (dcatSource.getId() == null) {
			dcatSource.setId("http://dcat.difi.no/dcatSource_" + UUID.randomUUID().toString());
		}
		if (dcatSource.getGraph() == null) {
			dcatSource.setGraph("http://dcat.difi.no/dcatSource_" + UUID.randomUUID().toString());
		}

		String query = String.join("\n",
				"delete {",
				"	graph <http://dcat.difi.no/usersGraph/> {",
				"		?dcatSource difiMeta:graph  ?originalDcatGraphUri. ",
				"		?dcatSource difiMeta:url  ?originalUrl.",
				"		?dcatSource rdfs:comment  ?originalDescription. ",
				"	}",
				"}",
				"insert {",
				"     graph <http://dcat.difi.no/usersGraph/> {",
				"          ?user difiMeta:dcatSource ?dcatSource .",
				"          ?dcatSource a difiMeta:DcatSource ; ",
				"                             difiMeta:graph ?dcatGraphUri; ",
				"                             difiMeta:url ?url;",
				"					rdfs:comment ?description",
				"",
				"",
				" . ",
				"     }",
				"} where {",
				"           BIND(IRI(?dcatSourceUri) as ?dcatSource)",
				"           ?user foaf:accountName ?username",
				"		OPTIONAL{ ?dcatSource difiMeta:graph  ?originalDcatGraphUri} ",
				"		OPTIONAL{ ?dcatSource difiMeta:url  ?originalUrl} ",
				"		OPTIONAL{ ?dcatSource rdfs:comment  ?originalDescription} ",

				"}");


		Map<String, String> map = new HashMap<>();

		map.put("username", dcatSource.getUser());
		map.put("dcatSourceUri", dcatSource.getId());
		map.put("dcatGraphUri", dcatSource.getGraph());
		map.put("description", dcatSource.getDescription());

		map.put("url", dcatSource.getUrl());


		fuseki.sparqlUpdate(query, map);

		if (fuseki.ask("ask { ?a ?b <" + dcatSource.getId() + ">}")) {
			return dcatSource;
		} else {
			//@TODO throw exception?
			return null;
		}

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


		// throw exception if the user has a dcatSource.


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