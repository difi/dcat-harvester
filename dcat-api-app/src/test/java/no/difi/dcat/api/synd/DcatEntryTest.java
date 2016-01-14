package no.difi.dcat.api.synd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.castor.CastorMarshaller;

/**
 * Created by havardottestad on 13/01/16.
 */
public class DcatEntryTest {

	@Test
	public void testDcatMarshaling() throws Exception {
		XmlConverter converter = new XmlConverter();
        CastorMarshaller marshaller = new CastorMarshaller();
        Resource resource = new ClassPathResource("mapping.xml");
        marshaller.setMappingLocation(resource);
        marshaller.afterPropertiesSet();
        converter.setMarshaller(marshaller);
        converter.setUnmarshaller(marshaller);
        
        DcatEntry dcatEntry = new DcatEntry(new Date(), "Test", "123456789", "testing", Arrays.asList("test", "testing", "tests"), Arrays.asList("xml", "plaintext"));
        
        String xml = converter.doMarshaling(dcatEntry);
        
        assertTrue(xml.contains("<datanorge:dcat xmlns:datanorge=\"http://data.norge.no\">"));
        assertTrue(xml.contains("<datanorge:subject>testing</datanorge:subject>"));
        assertTrue(xml.contains("<datanorge:keyword>test</datanorge:keyword>"));
	}
	
	@Test
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