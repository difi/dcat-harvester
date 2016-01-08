package no.difi.dcat.harvester.crawler;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.DifiMeta;
import no.difi.dcat.harvester.validation.DcatValidation;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.springframework.test.util.AssertionErrors.assertEquals;

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
		}).when(adminDataStore).addCrawlResults(Mockito.any(), Mockito.any());

		CrawlerResultHandler crawlerResultHandler = new CrawlerResultHandler(dcatDataStore, adminDataStore);

		crawlerResultHandler.process(new DcatSource("", "", "", ""), ModelFactory.createDefaultModel());

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
		}).when(adminDataStore).addCrawlResults(Mockito.any(), Mockito.any());

		CrawlerResultHandler crawlerResultHandler = new CrawlerResultHandler(dcatDataStore, adminDataStore);
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("validation-test-data/test-perfect.rdf").getFile());
		Model model = FileManager.get().loadModel(file.getCanonicalPath());
		crawlerResultHandler.process(new DcatSource("", "", "", ""), model);

	}
}