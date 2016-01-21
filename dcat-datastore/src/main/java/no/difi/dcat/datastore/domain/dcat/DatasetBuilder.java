package no.difi.dcat.datastore.domain.dcat;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;

public class DatasetBuilder extends AbstractBuilder {

	public static Dataset create(Resource distribution, Resource dataset, Resource catalog) {
		Dataset created = new Dataset();
		
		if (dataset != null) {
			created.setId(dataset.getURI());
			created.setTitle(extractLanguageLiteral(dataset, DCTerms.title));
			created.setDescription(extractLanguageLiteral(dataset, DCTerms.description));
			created.setIssued(extractDate(dataset, DCTerms.issued));
			created.setModified(extractDate(dataset, DCTerms.modified));
			created.setLanguage(extractAsString(dataset, DCTerms.language));
			created.setLandingPage(extractAsString(dataset, DCAT.landingPage));
			created.setKeywords(extractMultipleLanguageLiterals(dataset, DCAT.keyword));
		}
		if (catalog != null) {
			created.setCatalog(CatalogBuilder.create(catalog));
		}
		
		return created;
	}
	
}
