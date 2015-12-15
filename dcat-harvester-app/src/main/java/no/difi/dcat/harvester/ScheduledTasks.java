package no.difi.dcat.harvester;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.difi.dcat.harvester.crawler.CrawlerDataSource;
import no.difi.dcat.harvester.crawler.CrawlerJob;
import no.difi.dcat.harvester.crawler.CrawlerResultHandler;
import no.difi.dcat.harvester.settings.FusekiSettings;

@Component
public class ScheduledTasks {
	
	@Autowired
	private FusekiSettings fusekiSettings;
	
	@Scheduled(cron = "*/5 * * * *") //run every 5 minutes
	public void runCrawlerJobs() {
		
		List<CrawlerJob> jobs = Arrays.asList(new CrawlerJob(new CrawlerResultHandler(fusekiSettings.getDcatServiceUri()), CrawlerDataSource.getDefault()));
		
		for (CrawlerJob job : jobs) {
			job.run();
		}
	}
}
