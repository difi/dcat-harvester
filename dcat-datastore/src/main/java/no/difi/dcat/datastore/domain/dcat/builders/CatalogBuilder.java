package no.difi.dcat.datastore.domain.dcat.builders;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;

import no.difi.dcat.datastore.domain.dcat.Catalog;
import no.difi.dcat.datastore.domain.dcat.vocabulary.DCAT;

public class CatalogBuilder extends AbstractBuilder {
	
	public static Catalog create(Resource catalog) {
		Catalog created = new Catalog();
		
		if (catalog != null) {
			created.setId(catalog.getURI());
			created.setTitle(extractLanguageLiteral(catalog, DCTerms.title));
			created.setDescription(extractLanguageLiteral(catalog, DCTerms.description));
			created.setIssued(extractDate(catalog, DCTerms.issued));
			created.setModified(extractDate(catalog, DCTerms.modified));
			created.setLanguages(extractMultipleStrings(catalog, DCTerms.language));
			created.setPublisher(extractPublisher(catalog));
			created.setHomePage(extractDocument(catalog, FOAF.homepage));
			created.setThemeTaxonomies(extractMultipleStrings(catalog, DCAT.themeTaxonomy));
		}
		
		return created;
	}

}
