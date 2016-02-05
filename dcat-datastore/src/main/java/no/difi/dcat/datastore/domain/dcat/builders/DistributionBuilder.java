package no.difi.dcat.datastore.domain.dcat.builders;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import no.difi.dcat.datastore.domain.dcat.Distribution;
import no.difi.dcat.datastore.domain.dcat.vocabulary.DCAT;

public class DistributionBuilder extends AbstractBuilder {
	
	private Model model;
	
	public DistributionBuilder(Model model) {
		this.model = model;
	}
	
	public List<Distribution> build() {
		List<Distribution> distributions = new ArrayList<>();
		
		ResIterator catalogIterator = model.listResourcesWithProperty(RDF.type, DCAT.Catalog);
		while (catalogIterator.hasNext()) {
			Resource catalog = catalogIterator.next();
			ResIterator datasetIterator = catalog.getModel().listResourcesWithProperty(RDF.type, DCAT.Dataset);
			while (datasetIterator.hasNext()) {
				Resource dataset = datasetIterator.next();
				ResIterator distributionIterator = dataset.getModel().listResourcesWithProperty(RDF.type, DCAT.Distribution);
				while (distributionIterator.hasNext()) {
					Resource distribution = distributionIterator.next();
					distributions.add(create(distribution, dataset, catalog));
				}
			}
		}
		
		return distributions;
		
	}

	public static Distribution create(Resource distribution, Resource dataset, Resource catalog) {	
		Distribution created = new Distribution();
		
		if (distribution != null) {
			created.setId(distribution.getURI());
			created.setTitle(extractLanguageLiteral(distribution, DCTerms.title));
			created.setDescription(extractLanguageLiteral(distribution, DCTerms.description));
			created.setAccessURL(extractAsString(distribution, DCAT.accessUrl));
			created.setLicense(extractAsString(distribution, DCTerms.license));
			created.setFormat(extractAsString(distribution, DCTerms.format));
		}
		if (dataset != null && dataset != null) {
			created.setDataset(DatasetBuilder.create(dataset, catalog));
		}
		
		return created;
	}

}
