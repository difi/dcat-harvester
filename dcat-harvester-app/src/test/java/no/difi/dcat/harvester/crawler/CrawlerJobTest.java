package no.difi.dcat.harvester.crawler;

import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.DcatSource;

public class CrawlerJobTest {
	
	@Test
	public void testCrawlerJob() {
		DcatSource dcatSource = new DcatSource("http//dcat.difi.no/test", "Test", "src/test/resources/npolar.jsonld", "tester");
		
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		Mockito.doThrow(Exception.class).when(dcatDataStore).saveDataCatalogue(Mockito.anyObject(), Mockito.anyObject());
		
		CrawlerResultHandler handler = new CrawlerResultHandler(dcatDataStore);
		
		CrawlerJob job = new CrawlerJob(handler, dcatSource);
		
		job.run();
	}
	
	@Test
	public void testCrawlerResultHandlerWithNoException() {
		DcatSource dcatSource = new DcatSource("http//dcat.difi.no/test", "Test", "src/test/resources/npolar.jsonld", "tester");
		
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		
		CrawlerResultHandler handler = new CrawlerResultHandler(dcatDataStore);
		
		handler.process(dcatSource, ModelFactory.createDefaultModel());
	}
	
	@Test(expected=Exception.class)
	@Ignore //TODO: finne testfil som er gyldig
	public void testCrawlerResultHandlerWithExpectedException() {
		DcatSource dcatSource = new DcatSource("http//dcat.difi.no/test", "Test", "src/test/resources/npolar.jsonld", "tester");
		
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		Mockito.doThrow(Exception.class).when(dcatDataStore).saveDataCatalogue(Mockito.anyObject(), Mockito.anyObject());
		
		CrawlerResultHandler handler = new CrawlerResultHandler(dcatDataStore);
		
		handler.process(dcatSource, ModelFactory.createDefaultModel());
	}

}
