package no.difi.dcat.harvester.crawler.web;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.Elasticsearch;
import no.difi.dcat.datastore.Fuseki;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.harvester.crawler.Crawler;
import no.difi.dcat.harvester.crawler.CrawlerJob;
import no.difi.dcat.harvester.crawler.CrawlerJobFactory;
import no.difi.dcat.harvester.settings.ApplicationSettings;
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
	
	@Autowired
	private CrawlerJobFactory crawlerJobFactory;

	@Autowired
	private ApplicationSettings applicationSettings;
	
	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
	}
	
	@RequestMapping("/api/admin/harvest")
	public void harvestDataSoure(@RequestParam(value="id") String dcatSourceId) {
		logger.debug("Received request to harvest {}", dcatSourceId);
		Optional<DcatSource> dcatSource = adminDataStore.getDcatSourceById(dcatSourceId);
		if (dcatSource.isPresent()) {
			Client client = new Elasticsearch().returnElasticsearchTransportClient(applicationSettings.getElasticSearchHost(), applicationSettings.getElasticSearchPort());
			CrawlerJob job = crawlerJobFactory.createCrawlerJob(dcatSource.get(), client);
			crawler.execute(job);
			client.close();
		} else {
			logger.warn("No stored dcat source {}", dcatSource.toString());
		}
	}
	
	@RequestMapping("/api/admin/harvest-all")
	public void harvestDataSoure() {
		logger.debug("Received request to harvest all dcat sources");
		
		Client client = new Elasticsearch().returnElasticsearchTransportClient(applicationSettings.getElasticSearchHost(), applicationSettings.getElasticSearchPort());
		
		List<DcatSource> dcatSources = adminDataStore.getDcatSources();
		for (DcatSource dcatSource : dcatSources) {
			CrawlerJob job = crawlerJobFactory.createCrawlerJob(dcatSource, client);
			crawler.execute(job);
		}
		client.close();
		logger.debug("Finished all crawler jobs");
	}
}