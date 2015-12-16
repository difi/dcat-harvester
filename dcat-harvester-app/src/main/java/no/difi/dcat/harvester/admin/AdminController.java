package no.difi.dcat.harvester.admin;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.DcatSource;
import no.difi.dcat.datastore.Fuseki;
import no.difi.dcat.harvester.settings.FusekiSettings;

@RestController
public class AdminController {

	@Autowired
	private FusekiSettings fusekiSettings;
	private AdminDataStore adminDataStore;

	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
	}

	@RequestMapping("/api/admin/data-sources")
	public ResponseEntity<List<DcatSource>> getDataSources() {
		return new ResponseEntity<List<DcatSource>>(adminDataStore.getDcatSources(), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/admin/data-source", method = RequestMethod.POST)
	public void addDataSource(@RequestBody DcatSource dcatSource) {
		adminDataStore.addDcatSource(dcatSource);
	}

	@RequestMapping(value = "/api/admin/data-source", method = RequestMethod.DELETE)
	public void deleteDataSource(@RequestBody DcatSource dcatSource) {
		adminDataStore.deleteDcatSource(dcatSource.getName());
	}

}
