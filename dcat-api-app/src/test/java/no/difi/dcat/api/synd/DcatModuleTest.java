package no.difi.dcat.api.synd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.junit.Test;

/**
 * Created by havardottestad on 13/01/16.
 */
public class DcatModuleTest {

	@Test
	public void testGetInstance() throws Exception {

		String file = this.getClass().getClassLoader().getResource("test-perfect.rdf").getFile();

		Model m = FileManager.get().loadModel(file);
		m.write(System.out, "TTL");
		DcatModule instance = DcatModule.getInstance(m.getResource("http://nobelprize.org/datasets/dcat#ds1"));

		assertEquals("Subject should be 'Linked Nobel prizes'", "Linked Nobel prizes",instance.getSubject());
		assertEquals("Subject should be 'Nobel Media AB'", "Nobel Media AB",instance.getPublisher());

		List<String> expectedKeywords = Arrays.asList("prize", "science", "Nobel prize");
		assertTrue("Keyword list should be \"prize\", \"science\", \"Nobel prize\"", expectedKeywords.containsAll(instance.getKeywords()) && instance.getKeywords().containsAll(expectedKeywords));


	}
}