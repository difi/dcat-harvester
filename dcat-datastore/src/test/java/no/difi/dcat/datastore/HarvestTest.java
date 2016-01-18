package no.difi.dcat.datastore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;

import no.difi.dcat.datastore.domain.DcatSource.Harvest;
import no.difi.dcat.datastore.domain.DcatSource.HarvestComparator;

public class HarvestTest {
	
	@Test
	public void testHarvestComparator() {
		
		Harvest h1 = new Harvest(ResourceFactory.createResource("http://dcat.difi.no/dcatSource_h1"), "2014-01-01T12:00:00.000+00:00", "h1");
		Harvest h2 = new Harvest(ResourceFactory.createResource("http://dcat.difi.no/dcatSource_h2"), "2015-01-01T12:00:00.000+00:00", "h2");
		Harvest h3 = new Harvest(ResourceFactory.createResource("http://dcat.difi.no/dcatSource_h3"), "2016-01-01T12:00:00.000+00:00", "h3");
		
		List<Harvest> harvested = Arrays.asList(h1, h3, h2);
		Optional<Harvest> harvest = harvested.stream().max(new HarvestComparator());
		
		
		assertTrue("Expected harvest to be present", harvest.isPresent());
		assertEquals("Expected \"h3\" to be the latest harvest", h3, harvest.get());
	}
}
