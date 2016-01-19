package no.difi.dcat.harvester.crawler;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.DifiMeta;
import no.difi.dcat.harvester.crawler.handlers.FusekiResultHandler;
import no.difi.dcat.harvester.validation.DcatValidation;

/**
 * Created by havardottestad on 04/01/16.
 */
public class CrawlerResultHandlerTest {

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testValidation() throws Exception {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("validation-test-data/").getFile());
		Arrays.stream(file.listFiles((f) -> f.getName().endsWith(".rdf"))).forEach((f) -> {
			Model model = null;
			try {
				model = FileManager.get().loadModel(f.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}

			DcatValidation.validate(model, (error) -> System.out.println(error));
		});

	}

	@Test
	public void testValidationLoggingError() {
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		Mockito.doNothing().when(dcatDataStore).saveDataCatalogue(Mockito.any(), Mockito.any());


		AdminDataStore adminDataStore = Mockito.mock(AdminDataStore.class);
//            Mockito.doNothing().when(adminDataStore).addCrawlResults(Mockito.any(), Mockito.any());
		Mockito.doAnswer((invocationOnMock) -> {

			Resource status = (Resource) invocationOnMock.getArguments()[1];

			assertEquals("Since the provided dcat model is empty there should be validation errors.", DifiMeta.error, status);

			return null;
		}).when(adminDataStore).addCrawlResults(Mockito.any(), Mockito.any(), Mockito.any());

		FusekiResultHandler crawlerResultHandler = new FusekiResultHandler(dcatDataStore, adminDataStore);

		crawlerResultHandler.process(new DcatSource("", "", "", "", ""), ModelFactory.createDefaultModel());

	}


	@Test
	public void testValidationLoggingWarning() throws IOException {
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		Mockito.doNothing().when(dcatDataStore).saveDataCatalogue(Mockito.any(), Mockito.any());


		AdminDataStore adminDataStore = Mockito.mock(AdminDataStore.class);
//            Mockito.doNothing().when(adminDataStore).addCrawlResults(Mockito.any(), Mockito.any());
		Mockito.doAnswer((invocationOnMock) -> {

			Resource status = (Resource) invocationOnMock.getArguments()[1];

			assertEquals("The test-perfect.rdf file should give some warnings", DifiMeta.warning, status);

			return null;
		}).when(adminDataStore).addCrawlResults(Mockito.any(), Mockito.any(), Mockito.any());

		FusekiResultHandler crawlerResultHandler = new FusekiResultHandler(dcatDataStore, adminDataStore);
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("validation-test-data/test-perfect.rdf").getFile());
		Model model = FileManager.get().loadModel(file.getCanonicalPath());
		crawlerResultHandler.process(new DcatSource("", "", "", "", ""), model);

	}


	@Test
	public void testValidationLoggingSyntaxError() throws IOException {
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		Mockito.doNothing().when(dcatDataStore).saveDataCatalogue(Mockito.any(), Mockito.any());


		AdminDataStore adminDataStore = Mockito.mock(AdminDataStore.class);
//            Mockito.doNothing().when(adminDataStore).addCrawlResults(Mockito.any(), Mockito.any());
		Mockito.doAnswer((invocationOnMock) -> {

			Resource status = (Resource) invocationOnMock.getArguments()[1];
			String message = (String) invocationOnMock.getArguments()[2];

			assertEquals("Thesyntax-error.jsonld file should give a syntax error", DifiMeta.syntaxError, status);
			assertEquals("Should be the following syntax error", "[line: 1, col: 23] Unrecognized token 'fewjkfjewkl': was expecting 'null', 'true', 'false' or NaN", message);

			return null;
		}).when(adminDataStore).addCrawlResults(Mockito.any(), Mockito.any(), Mockito.any());

		FusekiResultHandler crawlerResultHandler = new FusekiResultHandler(dcatDataStore, adminDataStore);
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("syntax-error.jsonld").getFile());

		CrawlerJob crawlerJob = new CrawlerJob(new DcatSource("", "", file.getCanonicalPath(), "", ""), adminDataStore);

		crawlerJob.run();



	}


	@Test
	public void testValidationLoggingNetworkError() throws IOException {
		DcatDataStore dcatDataStore = Mockito.mock(DcatDataStore.class);
		Mockito.doNothing().when(dcatDataStore).saveDataCatalogue(Mockito.any(), Mockito.any());

		final boolean[] addCrawlResultsDidRun = {false};


		AdminDataStore adminDataStore = Mockito.mock(AdminDataStore.class);

		Mockito.doAnswer((invocationOnMock) -> {
			addCrawlResultsDidRun[0] = true;
			Resource status = (Resource) invocationOnMock.getArguments()[1];

			assertEquals("The host example.com doesn't exist and should produce a network error", DifiMeta.networkError, status);

			return null;
		}).when(adminDataStore).addCrawlResults(Mockito.any(), Mockito.any(), Mockito.any());

		FusekiResultHandler crawlerResultHandler = new FusekiResultHandler(dcatDataStore, adminDataStore);
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("syntax-error.jsonld").getFile());

		new CrawlerJob(new DcatSource("", "", "http://example.com/nothing.jsonld", "", ""), adminDataStore).run();
		new CrawlerJob(new DcatSource("", "", "http://fje389403wlkfklewfl.local/nothing.jsonld", "", ""), adminDataStore).run();
		new CrawlerJob(new DcatSource("", "", "http://localhost:9452/nothing.jsonld", "", ""), adminDataStore).run();

		assertTrue("The addCrawlResults was not called", addCrawlResultsDidRun[0]);
	}


}