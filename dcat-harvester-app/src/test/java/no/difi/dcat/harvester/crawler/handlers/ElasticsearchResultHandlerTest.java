package no.difi.dcat.harvester.crawler.handlers;

import no.difi.dcat.datastore.domain.DcatSource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by havardottestad on 20/01/16.
 */
public class ElasticsearchResultHandlerTest {

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testInvertDatasetRelation() throws Exception {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("validation-test-data/test-perfect.rdf").getFile());

		Model model = FileManager.get().loadModel(file.getCanonicalPath());

		ElasticsearchResultHandler elasticsearchResultHandler = new ElasticsearchResultHandler();
		elasticsearchResultHandler.process(new DcatSource(), model);

		//model.write(System.out, "TTL");

	}
}