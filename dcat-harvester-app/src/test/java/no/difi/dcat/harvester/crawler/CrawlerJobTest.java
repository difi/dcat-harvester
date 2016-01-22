package no.difi.dcat.harvester.crawler;

import no.difi.dcat.datastore.AdminDataStore;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.harvester.crawler.handlers.FusekiResultHandler;

public class CrawlerJobTest {
	
	@Test
	public void testCrawlerJob() {
		DcatSource dcatSource = new DcatSource("http//dcat.difi.no/test", "Test", "src/test/resources/npolar.jsonld", "tester", "123456789");
		
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		Mockito.doThrow(Exception.class).when(dcatDataStore).saveDataCatalogue(Mockito.anyObject(), Mockito.anyObject());

		FusekiResultHandler handler = new FusekiResultHandler(dcatDataStore, null);
		
		CrawlerJob job = new CrawlerJob(dcatSource, null, handler);
		
		job.run();
	}
	
	@Test
	public void testCrawlerResultHandlerWithNoException() {
		DcatSource dcatSource = new DcatSource("http//dcat.difi.no/test", "Test", "src/test/resources/npolar.jsonld", "tester", null);
		
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		AdminDataStore adminDataStore = Mockito.mock(AdminDataStore.class);

		FusekiResultHandler handler = new FusekiResultHandler(dcatDataStore, adminDataStore);
		
		handler.process(dcatSource, ModelFactory.createDefaultModel());
	}
	
	@Test
	public void testCrawlerJobWithBrregLink() {
		DcatSource dcatSource = new DcatSource("http//dcat.difi.no/test", "Test", "src/test/resources/brreg-link.jsonld", "tester", "");
		
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		AdminDataStore adminDataStore = Mockito.mock(AdminDataStore.class);
		
		FusekiResultHandler handler = new FusekiResultHandler(dcatDataStore, null);
		
		handler.process(dcatSource, ModelFactory.createDefaultModel());
		
		CrawlerJob job = new CrawlerJob(dcatSource, adminDataStore, handler);
		job.run();
	}

}
