package no.difi.dcat.api.synd;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.JenaException;

public class PropertyExtractor {

	public static String extractExactlyOneStringOrNull(Resource resource, Property... p) {
		for (int i = 0; i < p.length; i++) {
			StmtIterator stmtIterator = resource.listProperties(p[i]);
			if (i == p.length - 1) {
				try {
					if (stmtIterator.hasNext()) {
						return stmtIterator.next().getString();
					}
				} catch (JenaException e) {
					return null;
				}
			} else {
				try {
					if (stmtIterator.hasNext()) {
						resource = stmtIterator.next().getResource();
					}
				} catch (JenaException e) {
					return null;
				}
			}
		}
		return null;
	}
	
}
