package no.difi.dcat.datastore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.User;

/**
 * @author by havardottestad, sebnmuller
 */
public class AdminDcatDataService {


	private final AdminDataStore adminDataStore;
	private final DcatDataStore dcatDataStore;
	private Elasticsearch elasticsearch;


	private final Logger logger = LoggerFactory.getLogger(AdminDcatDataService.class);

	public AdminDcatDataService(AdminDataStore adminDataStore, DcatDataStore dcatDataStore) {
		this.adminDataStore = adminDataStore;
		this.dcatDataStore = dcatDataStore;
	}



	/**
	 * @param dcatSourceId
	 */
	public void deleteDcatSource(String dcatSourceId, User loggedInUser) {


		Optional<DcatSource> dcatSourceById = adminDataStore.getDcatSourceById(dcatSourceId);
		
		if(!dcatSourceById.isPresent()) return;
		DcatSource dcatSource = dcatSourceById.get();

		if(!dcatSource.getUser().equalsIgnoreCase(loggedInUser.getUsername()) && !loggedInUser.isAdmin()){
			throw new SecurityException(loggedInUser.getUsername()+" tried to delete a DcatSource belonging to "+dcatSource.getUser());
		}

		String query = String.join("\n",
				"delete {",
				"	graph <http://dcat.difi.no/usersGraph/> {",
				"		?dcatSource ?b ?c.",
				"		?c ?d ?e.",
				"		?f ?g ?dcatSource ." ,

				"	}",
				"} where {",
				"           BIND(IRI(?dcatSourceUri) as ?dcatSource)",
				"		?dcatSource ?b ?c.",
				"		OPTIONAL{" ,
				"			?c ?d ?e." ,
				"		}",
				"		OPTIONAL{" ,
				"			?f ?g ?dcatSource ." ,
				"		}",

				"}");

		Map<String, String> map = new HashMap<>();

		map.put("dcatSourceUri", dcatSource.getId());
		
		adminDataStore.fuseki.sparqlUpdate(query, map);
		dcatDataStore.deleteDataCatalogue(dcatSource);

		try {
			
			logger.info("[crawler_admin] [info] connecting to elasticssearch...");
		
			elasticsearch = new Elasticsearch("elasticsearch", 9300);
			logger.info("[crawler_admin] [info] connected to elasticssearch");
		} catch (Exception e) {
			logger.error("[crawler_admin] [error] failed to connect to elasticsearch");
			StackTraceElement[] stackTrace = e.getStackTrace();
            
            for (int j = 0; j < stackTrace.length; j++) {
            	logger.error(stackTrace[j].toString());
			}
		}
		
		if(elasticsearch.deleteDocument(".kibana", "search", dcatSourceId)) {
			logger.info("[crawler_admin] [success] Deleted DCAT source from Kibana: {}", dcatSource.toString());
		} else {
			logger.error("[crawler_admin] [fail] DCAT source was not deleted from Kibana: {}", dcatSource.toString());
		}
		
		if(elasticsearch.deleteDocument("dcat", "dataset", dcatSourceId)) {
			logger.info("[crawler_admin] [success] Deleted DCAT source from Kibana: {}", dcatSource.toString());
		} else {
			logger.error("[crawler_admin] [fail] DCAT source was not deleted from Kibana: {}", dcatSource.toString());
		}
		
		elasticsearch.deleteAllFromSource("dcat", "dataset", dcatSourceId);
		
		if (adminDataStore.fuseki.ask("ask { ?dcatSourceUri foaf:accountName ?dcatSourceUri}", map)) { 
			logger.error("[crawler_admin] [fail] DCAT source was not deleted from Fuseki: {}", dcatSource.toString());
		} else {
			logger.info("[crawler_admin] [success] Deleted DCAT source from Fuseki: {}", dcatSource.toString());
		}
		
	}
	
	public void deleteUser(String usernameToDelete, User adminUser) {
		
		if (adminUser.isAdmin()) {
				
				String query = String.join("\n",
						"delete {",
						"	graph <http://dcat.difi.no/usersGraph/> {",
						"		?user ?b ?c.",
						"	}",
						"} where {",
						"		?user foaf:accountName ?username;",
						"		?b ?c .",
						"		FILTER (NOT EXISTS {?user difiMeta:dcatSource ?dcatSource})",
						"}");

				Map<String, String> map = new HashMap<>();

				map.put("username", usernameToDelete);

				adminDataStore.fuseki.sparqlUpdate(query, map);	
				if (adminDataStore.fuseki.ask("ask { ?user foaf:accountName ?username}", map)) {
					logger.error("[user_admin] [fail] User was not deleted: {}", adminUser.toString());
				} else {
					logger.info("[user_admin] [success] Deleted user: {}", adminUser.toString());
			}

		} else {
			logger.warn("ADMIN role is required to delete users");
		}
	}
}
