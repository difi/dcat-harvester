package no.difi.dcat.datastore.domain.dcat.builders;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.domain.dcat.Distribution;
import no.difi.dcat.datastore.domain.dcat.vocabulary.DCAT;

public class DistributionBuilder extends AbstractBuilder {

	private Model model;
    private final Logger logger = LoggerFactory.getLogger(DistributionBuilder.class);


	public DistributionBuilder(Model model) {
		this.model = model;
	}

	public List<Distribution> build() {
		List<Distribution> distributions = new ArrayList<>();

		ResIterator catalogIterator = model.listResourcesWithProperty(RDF.type, DCAT.Catalog);
		while (catalogIterator.hasNext()) {
			Resource catalog = catalogIterator.next();

			//ResIterator datasetIterator = catalog.getModel().listResourcesWithProperty(RDF.type, DCAT.Dataset);
			StmtIterator datasetIterator = catalog.listProperties(DCAT.dataset);


			while (datasetIterator.hasNext()) {
				Resource dataset = datasetIterator.next().getResource();
				StmtIterator distributionIterator = dataset.listProperties(DCAT.distribution);

				while (distributionIterator.hasNext()) {
					Statement next = distributionIterator.nextStatement();

					if (next.getObject().isResource()) {
						Resource distribution = next.getResource();
						Distribution[] dist = Distribution.splitFormat(create(distribution, dataset, catalog));

						logger.debug("Distribution {} was split into {} pieces", distribution, dist.length);
						for (int i = 0; i < dist.length; i++) {
							distributions.add(dist[i]);
						}
					}
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
			created.setFormat(extractMultipleStrings(distribution, DCTerms.format));
			created.setDownloadURL(extractMultipleStrings(distribution, DCAT.downloadURL));
			created.setConformsTo(extractMultipleStrings(distribution, DCTerms.conformsTo));
			created.setPages(extractMultipleDocuments(distribution, FOAF.page)); 
		}
		if (dataset != null && catalog != null) {
			created.setDataset(DatasetBuilder.create(dataset, catalog));
		}

		return created;
	}
}
