package no.difi.dcat.harvester.crawler;

import no.difi.dcat.datastore.AdminDataStore;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.domain.DcatSource;

public class CrawlerJobTest {
	
	@Test
	public void testCrawlerJob() {
		DcatSource dcatSource = new DcatSource("http//dcat.difi.no/test", "Test", "src/test/resources/npolar.jsonld", "tester", "123456789");
		
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		Mockito.doThrow(Exception.class).when(dcatDataStore).saveDataCatalogue(Mockito.anyObject(), Mockito.anyObject());

		CrawlerResultHandler handler = new CrawlerResultHandler(dcatDataStore, null);
		
		CrawlerJob job = new CrawlerJob(handler, dcatSource, null);
		
		job.run();
	}
	
	@Test
	public void testCrawlerResultHandlerWithNoException() {
		DcatSource dcatSource = new DcatSource("http//dcat.difi.no/test", "Test", "src/test/resources/npolar.jsonld", "tester", null);
		
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		AdminDataStore adminDataStore = Mockito.mock(AdminDataStore.class);

		CrawlerResultHandler handler = new CrawlerResultHandler(dcatDataStore, adminDataStore);
		
		handler.process(dcatSource, ModelFactory.createDefaultModel());
	}
	
	@Test(expected=Exception.class)
	@Ignore //TODO: finne testfil som er gyldig
	public void testCrawlerResultHandlerWithExpectedException() {
		DcatSource dcatSource = new DcatSource("http//dcat.difi.no/test", "Test", "src/test/resources/npolar.jsonld", "tester", "");
		
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		Mockito.doThrow(Exception.class).when(dcatDataStore).saveDataCatalogue(Mockito.anyObject(), Mockito.anyObject());
		
		CrawlerResultHandler handler = new CrawlerResultHandler(dcatDataStore, null);
		
		handler.process(dcatSource, ModelFactory.createDefaultModel());
	}

}
