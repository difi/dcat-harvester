package no.difi.dcat.datastore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.User;

/**
 * Created by havardottestad on 07/01/16.
 */
public class AdminDcatDataService {


	private final AdminDataStore adminDataStore;
	private final DcatDataStore dcatDataStore;


	private final Logger logger = LoggerFactory.getLogger(AdminDcatDataService.class);

	public AdminDcatDataService(AdminDataStore adminDataStore, DcatDataStore dcatDataStore) {
		this.adminDataStore = adminDataStore;
		this.dcatDataStore = dcatDataStore;
	}



	/**
	 * @param dcatSourceId
	 */
	// TODO: add elasticsearch delete index call
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

		// TODO: create client, when done, close it.
		Client client = adminDataStore.elasticsearch.elasticsearchTransportClient("localhost", 9200, "is this", "necessary");
		dcatDataStore.deleteDataCatalogue(dcatSource, client, dcatSource.getId());
		client.close();
		
		if (adminDataStore.fuseki.ask("ask { ?dcatSourceUri foaf:accountName ?dcatSourceUri}", map)) { 
			logger.error("[user_admin] [fail] DCAT source was not deleted: {}", dcatSource.toString());
		} else {
			logger.info("[user_admin] [success] Deleted DCAT source: {}", dcatSource.toString());
		}
		
	}
	
	public void deleteUser(String username, User user) {
		
		if ("ADMIN".equalsIgnoreCase(user.getRole())) {
				
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

				map.put("username", username);

				adminDataStore.fuseki.sparqlUpdate(query, map);	
				if (adminDataStore.fuseki.ask("ask { ?user foaf:accountName ?username}", map)) {
					logger.error("[user_admin] [fail] User was not deleted: {}", user.toString());
				} else {
					logger.info("[user_admin] [success] Deleted user: {}", user.toString());
				}
				
		} else {
			logger.warn("ADMIN role is required to delete users");
		}
	}
}
