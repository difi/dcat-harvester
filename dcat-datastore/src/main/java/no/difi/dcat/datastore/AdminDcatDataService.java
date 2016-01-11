package no.difi.dcat.datastore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
	public void deleteDcatSource(String dcatSourceId, User loggedInUser) {
		logger.trace("Deleting dcat source {}", dcatSourceId);

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
	}
	
	public void deleteUser(String username, User user) {
		logger.trace("Deleting user {}", username);
		
		if (user.getRole().equalsIgnoreCase("ADMIN")) {
				
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
					
				} else {
					logger.error("User {} was not deleted", username);
				}
				
		} else {
			logger.warn("ADMIN role is required to delete users");
		}
	}
}
