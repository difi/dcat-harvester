package no.difi.dcat.api.synd;

import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.CollectionFactory;
import org.apache.jena.util.FileManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by havardottestad on 13/01/16.
 */
public class DcatEntryTest {

	@org.junit.Before
	public void setUp() throws Exception {

	}

	@org.junit.After
	public void tearDown() throws Exception {

	}

	@org.junit.Test
	public void testGetInstance() throws Exception {

		String file = this.getClass().getClassLoader().getResource("test-perfect.rdf").getFile();

		Model m = FileManager.get().loadModel(file);
		m.write(System.out, "TTL");
		DcatEntry instance = DcatEntry.getInstance(m.getResource("http://nobelprize.org/datasets/dcat#ds1"));

		assertEquals("Subject should be 'Linked Nobel prizes'", "Linked Nobel prizes",instance.getSubject());
		assertEquals("Subject should be 'Nobel Media AB'", "Nobel Media AB",instance.getPublisher());

		List<String> expectedKeywords = Arrays.asList("prize", "science", "Nobel prize");
		assertTrue("Keyword list should be \"prize\", \"science\", \"Nobel prize\"", expectedKeywords.containsAll(instance.getKeywords()) && instance.getKeywords().containsAll(expectedKeywords));


	}
}