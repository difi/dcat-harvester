package no.difi.dcat.harvester;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.difi.dcat.harvester.admin.DataSourcesManager;
import no.difi.dcat.harvester.crawler.CrawlerDataSource;
import no.difi.dcat.harvester.crawler.CrawlerJob;
import no.difi.dcat.harvester.crawler.CrawlerResultHandler;
import no.difi.dcat.harvester.service.FusekiController;
import no.difi.dcat.harvester.settings.FusekiSettings;

@Component
public class ScheduledTasks {
	
	@Autowired
	private FusekiSettings fusekiSettings;
	
	@Scheduled(cron = "0 */5 * * * *") //run every 5 minutes
	public void runCrawlerJobs() {
		
		FusekiController fusekiController = new FusekiController(fusekiSettings.getAdminServiceUri());
		DataSourcesManager manager = new DataSourcesManager(fusekiController);
		
		for (CrawlerDataSource dataSource : manager.getDataSources()) {
			CrawlerJob job = new CrawlerJob(new CrawlerResultHandler(fusekiSettings.getDcatServiceUri()), dataSource);
			job.run();
		}
	}
}
