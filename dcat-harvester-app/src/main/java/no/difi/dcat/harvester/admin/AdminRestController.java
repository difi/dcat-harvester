package no.difi.dcat.harvester.admin;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.DcatSource;
import no.difi.dcat.datastore.Fuseki;
import no.difi.dcat.harvester.crawler.CrawlerJob;
import no.difi.dcat.harvester.crawler.CrawlerResultHandler;
import no.difi.dcat.harvester.settings.FusekiSettings;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class AdminRestController {

	@Autowired
	private FusekiSettings fusekiSettings;
	private AdminDataStore adminDataStore;
	
	private final Logger logger = LoggerFactory.getLogger(AdminRestController.class);

	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
	}

	@RequestMapping("/api/admin/dcat-sources")
	public ResponseEntity<List<DcatSource>> getDataSources() {
		return new ResponseEntity<List<DcatSource>>(adminDataStore.getDcatSources(), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/admin/dcat-source", method = RequestMethod.POST)
	public void addDataSource(@RequestBody DcatSource dcatSource) {
		adminDataStore.addDcatSource(dcatSource);
	}

	@RequestMapping(value = "/api/admin/dcat-source", method = RequestMethod.DELETE)
	public void deleteDataSource(@RequestBody DcatSource dcatSource) {
		adminDataStore.deleteDcatSource(dcatSource.getName());
	}
	
	@RequestMapping("/api/admin/harvest")
	public void harvestDataSoure(@RequestParam("name") String dcatSourceName) {
		logger.debug("Received request to harvest {}", dcatSourceName);
		Optional<DcatSource> dcatSource = adminDataStore.getDcatSourceByName(dcatSourceName);
		if (dcatSource.isPresent()) {
			CrawlerResultHandler handler = new CrawlerResultHandler(fusekiSettings.getDcatServiceUri());
			CrawlerJob job = new CrawlerJob(handler, dcatSource.get());
			try {
				logger.debug("Manually starting crawler job for {}", dcatSourceName);
				job.run();
			} catch (Exception e) {
				logger.error("Error running crawler manually", e);
			}
			logger.debug("Finished manuel crawler job for {}", dcatSourceName);
		} else {
			logger.warn("No stored dcat source with name {}", dcatSourceName);
		}
	}

}
