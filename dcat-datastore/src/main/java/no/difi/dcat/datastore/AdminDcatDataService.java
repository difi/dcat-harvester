package no.difi.dcat.datastore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import no.difi.dcat.datastore.domain.DcatSource;

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
	public void deleteDcatSource(String dcatSourceId) {
		logger.trace("Deleting dcat source {}", dcatSourceId);

		Optional<DcatSource> dcatSourceById = adminDataStore.getDcatSourceById(dcatSourceId);


		if(!dcatSourceById.isPresent()) return;
		DcatSource dcatSource = dcatSourceById.get();

		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String realuser = user.getUsername(); //get logged in username


		if(!dcatSource.getUser().equals(realuser)){
			throw new SecurityException(dcatSource.getUser()+" tried to delete a DcatSource belonging to "+realuser);
		}

		String query = String.join("\n",
				"delete {",
				"	graph <http://dcat.difi.no/usersGraph/> {",
				"		?dcatSource ?b ?c.",
				"		?c ?d ?e.",
				"	}",
				"} where {",
				"           BIND(IRI(?dcatSourceUri) as ?dcatSource)",
				"		?dcatSource ?b ?c.",
				"		OPTIONAL{" ,
				"			?c ?d ?e." ,
				"		}",
				"}");

		Map<String, String> map = new HashMap<>();

		map.put("dcatSourceUri", dcatSource.getId());

		adminDataStore.fuseki.sparqlUpdate(query, map);

		dcatDataStore.deleteDataCatalogue(dcatSource);
	}
	
	public void deleteUser(String username) {
		logger.trace("Deleting user {}", username);
		
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<GrantedAuthority> authority = user.getAuthorities().stream().filter((GrantedAuthority ga) -> ga.getAuthority().equalsIgnoreCase("ADMIN")).findAny();
		if (authority.isPresent()) {
				
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
