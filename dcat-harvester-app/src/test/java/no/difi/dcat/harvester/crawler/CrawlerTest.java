package no.difi.dcat.harvester.crawler;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.junit.Test;
import org.mockito.Mockito;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.domain.DcatSource;

public class CrawlerTest {
	
	@Test
	public void testCrawlerThreadPool() throws Exception {
		
		DcatSource dcatSource1 = new DcatSource("http//dcat.difi.no/test1", "Test1", "src/test/resources/npolar.jsonld", "tester");
		DcatSource dcatSource2 = new DcatSource("http//dcat.difi.no/test2", "Test2", "src/test/resources/npolar.jsonld", "tester");
		DcatSource dcatSource3 = new DcatSource("http//dcat.difi.no/test3", "Test3", "src/test/resources/npolar.jsonld", "tester");
		DcatSource dcatSource4 = new DcatSource("http//dcat.difi.no/test4", "Test4", "src/test/resources/npolar.jsonld", "tester");
		
		List<DcatSource> dcatSources = Arrays.asList(dcatSource1, dcatSource2, dcatSource3, dcatSource4);
		
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		AdminDataStore adminDataStore = Mockito.mock(AdminDataStore.class);

		CrawlerResultHandler handler = new CrawlerResultHandler(dcatDataStore, null);
		
		List<CrawlerJob> crawlerJobs = dcatSources.stream().map(dcatSource -> new CrawlerJob(handler, dcatSource, adminDataStore)).collect(Collectors.<CrawlerJob>toList());
		
		Crawler crawler = new Crawler();
		crawler.initialize();
		
		List<Future<?>> futures = crawler.execute(crawlerJobs);
		
		for (Future<?> future :futures) { 
			future.get(); 
		}
	}

}
