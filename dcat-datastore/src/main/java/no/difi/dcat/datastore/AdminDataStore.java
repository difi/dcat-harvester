package no.difi.dcat.datastore;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.DifiMeta;
import no.difi.dcat.datastore.domain.User;

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


		Map<String, String> map = new HashMap<>();

		String query = String.join("\n",
				"describe ?a ?user where {",
				"	?user difiMeta:dcatSource ?a.",
				"}"
		);
		Model dcatModel = fuseki.describe(query, map);

		System.out.println(DifiMeta.DcatSource);

		List<DcatSource> ret = new ArrayList<>();

		ResIterator resIterator = dcatModel.listResourcesWithProperty(RDF.type, DifiMeta.DcatSource);


		while(resIterator.hasNext()){
			String uri = resIterator.nextResource().getURI();
			ret.add(new DcatSource(dcatModel, uri));

		}

		return ret;

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
	 * @param user
	 */
	public URI addUser(User user) throws UserAlreadyExistsException {
		logger.trace("Adding user {}", user.getUsername());

		if (user.getId() == null || user.getId().isEmpty()) {
			user.setId("http://dcat.difi.no/user_" + UUID.randomUUID().toString());
		}	

		String query = String.join("\n",
				"insert {",
				"     graph <http://dcat.difi.no/usersGraph/> {",
				"           <" + user.getId() + "> foaf:accountName ?username;",
				"                               difiMeta:role ?role;",
				"                               difiMeta:email ?email;",
				"                               difiMeta:password ?password;" +
				"                               a difiMeta:User;",
				"            .",
				"     }",
				"} where {",
				"     FILTER(!EXISTS{?a foaf:accountName ?username})",
				"}"
		);

		Map<String, String> map = new HashMap<>();
		map.put("username", user.getUsername());
		map.put("role", user.getRole());
		map.put("password", user.getPassword());
		map.put("email", user.getEmail());

		fuseki.sparqlUpdate(query, map);

		if (fuseki.ask("ask { <" + user.getId() + "> ?b ?c}")) {
			return URI.create(user.getId());
		} else {
			throw new UserAlreadyExistsException(user.getUsername());
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
	
	public List<User> getUsers() {
		Map<String, String> map = new HashMap<>();
		String query = String.join("\n",
				"select ?userid ?username ?password ?email ?role where {",
				"     ?userid foaf:accountName ?username ;",
				"           difiMeta:password ?password ;",
				"			difiMeta:email ?email ;",
				"           difiMeta:role ?role ;",
				".",
				"}"
		);
		ResultSet results = fuseki.select(query, map);

		List<User> users = new ArrayList<>();
		while (results.hasNext()) {
			users.add(User.fromQuerySolution(results.next()));
		}

		return users;
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

	public void addCrawlResults(DcatSource dcatSource, Resource status, String message) {

		logger.trace("Adding crawl result to dcat source {}", dcatSource.getId());
		Map<String, String> map = new HashMap<>();

		String sparqlMessage = "";
		if(message != null){
			sparqlMessage = "rdfs:comment ?message;";
			map.put("message", message);
		}

		String query = String.join("\n",
				"insert {",
				"     graph <http://dcat.difi.no/usersGraph/> {",
				"           <" + dcatSource.getId() + "> difiMeta:harvested [",
				"			a difiMeta:Harvest;",
				"			dct:created ?dateCreated;",
				"			difiMeta:status <"+status.getURI()+">;",
				sparqlMessage,
				"			",
				"            ].",
				"     }",
				"} where {",
				"     BIND(NOW() as ?dateCreated)",
				"}"
		);


		fuseki.sparqlUpdate(query, map);



	}

	public User getUserObject(String username) {
		Map<String,String> userMap = getUser(username);
		return new User("", userMap.get("username"), "", "", userMap.get("role"));
	}
}