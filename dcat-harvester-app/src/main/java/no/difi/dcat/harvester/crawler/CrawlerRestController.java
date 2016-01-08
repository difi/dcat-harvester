package no.difi.dcat.harvester.crawler;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.Fuseki;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.harvester.settings.FusekiSettings;

@RestController
@CrossOrigin(origins = "*")
public class CrawlerRestController {

	@Autowired
	private FusekiSettings fusekiSettings;
	private AdminDataStore adminDataStore;
	
	private final Logger logger = LoggerFactory.getLogger(CrawlerRestController.class);

	@Autowired
	private Crawler crawler;
	
	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
	}
	
	@RequestMapping("/api/admin/harvest")
	public void harvestDataSoure(@RequestParam(value="id") String dcatSourceId) {
		logger.debug("Received request to harvest {}", dcatSourceId);
		Optional<DcatSource> dcatSource = adminDataStore.getDcatSourceById(dcatSourceId);
		if (dcatSource.isPresent()) {
			CrawlerResultHandler handler = new CrawlerResultHandler(fusekiSettings.getDcatServiceUri(), fusekiSettings.getAdminServiceUri());
			CrawlerJob job = new CrawlerJob(handler, dcatSource.get(), adminDataStore);
			logger.info("Start manual crawler job for {}", dcatSource.toString());
			crawler.execute(job);
			logger.info("Finished manual crawler job for {}", dcatSource.toString());
		} else {
			logger.warn("No stored dcat source with id {}", dcatSource.toString());
		}
	}
	
	@RequestMapping("/api/admin/harvest-all")
	public void harvestDataSoure() {
		logger.debug("Received request to harvest all dcat sources");
		
		CrawlerResultHandler handler = new CrawlerResultHandler(fusekiSettings.getDcatServiceUri(), fusekiSettings.getAdminServiceUri());
		List<DcatSource> dcatSources = adminDataStore.getDcatSources();
		for (DcatSource dcatSource : dcatSources) {
			CrawlerJob job = new CrawlerJob(handler, dcatSource, adminDataStore);
			crawler.execute(job);
		}
		logger.debug("Finished all crawler jobs");
	}
}