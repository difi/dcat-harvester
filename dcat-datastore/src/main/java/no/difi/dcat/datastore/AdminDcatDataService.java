package no.difi.dcat.datastore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
}
