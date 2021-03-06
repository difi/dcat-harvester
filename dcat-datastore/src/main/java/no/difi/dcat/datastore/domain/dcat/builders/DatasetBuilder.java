package no.difi.dcat.datastore.domain.dcat.builders;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import no.difi.dcat.datastore.domain.dcat.Dataset;
import no.difi.dcat.datastore.domain.dcat.Distribution;
import no.difi.dcat.datastore.domain.dcat.vocabulary.DCAT;

public class DatasetBuilder extends AbstractBuilder {

	private final Model model;
	private final String dcatSourceId;
	
	public DatasetBuilder(Model model, String dcatSourceId) {
		this.model = model;
		this.dcatSourceId = dcatSourceId;
	}
	
	public List<Dataset> build() {
		
		List<Dataset> datasets = new ArrayList<>();
		
		ResIterator catalogIterator = model.listResourcesWithProperty(RDF.type, DCAT.Catalog);
		while (catalogIterator.hasNext()) {
			Resource catalog = catalogIterator.next();
            StmtIterator datasetIterator = catalog.listProperties(DCAT.dataset);


			while (datasetIterator.hasNext()) {
				Resource dataset = datasetIterator.next().getResource();
				Dataset datasetObj = create(dataset, catalog, dcatSourceId);
				StmtIterator distributionIterator = dataset.listProperties(DCAT.distribution);
				List<Distribution> distributions = new ArrayList<>();
				while (distributionIterator.hasNext()) {
					Statement next = distributionIterator.nextStatement();


					if (next.getObject().isResource()) {
						Resource distribution = next.getResource();
						
						Distribution[] dist = Distribution.splitFormat(DistributionBuilder.create(distribution, null, null, dcatSourceId));

						for (int i = 0; i < dist.length; i++) {
							distributions.add(dist[i]);
						}
					}

				}
				datasetObj.setDistributions(distributions);
				datasets.add(datasetObj);
			}
		}
		
		return datasets;
	}
	
	public static Dataset create(Resource dataset, Resource catalog, String sourceId) {
		Dataset created = new Dataset();
		
		if (dataset != null) {
			created.setId(dataset.getURI());
			created.setTitle(extractLanguageLiteral(dataset, DCTerms.title));
			created.setDescription(extractLanguageLiteral(dataset, DCTerms.description));
			created.setIssued(extractDate(dataset, DCTerms.issued));
			created.setModified(extractDate(dataset, DCTerms.modified));
			created.setLanguages(extractMultipleStrings(dataset, DCTerms.language));
			created.setLandingPage(extractAsString(dataset, DCAT.landingPage));
			created.setKeywords(extractMultipleLanguageLiterals(dataset, DCAT.keyword));
			created.setContact(extractContact(dataset));
			created.setIdentifier(extractAsString(dataset, DCTerms.identifier));
			created.setAccessRights(extractAsString(dataset, DCTerms.accessRights));
			created.setFrequency(extractAsString(dataset, DCTerms.accrualPeriodicity));
			created.setThemes(extractMultipleStrings(dataset, DCAT.theme));
			created.setRelated(extractMultipleStrings(dataset, DCTerms.relation));
			created.setSpatial(extractMultipleStrings(dataset, DCTerms.spatial));
			created.setTemporal(extractTemporal(dataset));
			created.setPublisher(extractPublisher(dataset));
			created.setDcatSourceId(sourceId);
		}
		if (catalog != null) {
			created.setCatalog(CatalogBuilder.create(catalog));
		}

		return created;
	}
	
}
