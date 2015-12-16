package no.difi.dcat.harvester;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.DcatSource;
import no.difi.dcat.datastore.Fuseki;
import no.difi.dcat.harvester.crawler.CrawlerJob;
import no.difi.dcat.harvester.crawler.CrawlerResultHandler;
import no.difi.dcat.harvester.settings.FusekiSettings;

@Component
public class ScheduledTasks {
	
	@Autowired
	private FusekiSettings fusekiSettings;
	
	private AdminDataStore adminDataStore;
	private CrawlerResultHandler crawlerResultHandler;
	
	@PostConstruct
	public void initialize() {
		adminDataStore = new AdminDataStore(new Fuseki(fusekiSettings.getAdminServiceUri()));
		crawlerResultHandler = new CrawlerResultHandler(fusekiSettings.getDcatServiceUri());
	}
	
	@Scheduled(cron = "0 */5 * * * *") //run every 5 minutes
	public void runCrawlerJobs() {
		for (DcatSource dcatSource : adminDataStore.getDcatSources()) {
			CrawlerJob job = new CrawlerJob(crawlerResultHandler, dcatSource);
			job.run();
		}
	}
}
