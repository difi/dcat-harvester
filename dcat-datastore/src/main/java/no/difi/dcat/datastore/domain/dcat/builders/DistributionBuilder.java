package no.difi.dcat.datastore.domain.dcat.builders;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import no.difi.dcat.datastore.domain.dcat.Distribution;
import no.difi.dcat.datastore.domain.dcat.vocabulary.DCAT;

public class DistributionBuilder extends AbstractBuilder {

	private Model model;
	private final String dcatSourceId;


	public DistributionBuilder(Model model, String dcatSourceId) {
		this.model = model;
		this.dcatSourceId = dcatSourceId;
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
						distributions.add(create(distribution, dataset, catalog, dcatSourceId));
					}
				}
			}
		}

		return distributions;

	}

	public static Distribution create(Resource distribution, Resource dataset, Resource catalog, String dcatSourceId) {
		Distribution created = new Distribution();

		if (distribution != null) {
			created.setId(distribution.getURI());
			created.setTitle(extractLanguageLiteral(distribution, DCTerms.title));
			created.setDescription(extractLanguageLiteral(distribution, DCTerms.description));
			created.setAccessURL(extractAsString(distribution, DCAT.accessUrl));
			created.setLicense(extractAsString(distribution, DCTerms.license));
			created.setDistributionType(extractAsString(distribution, DCTerms.type));
			created.setFormat(extractMultipleStrings(distribution, DCTerms.format));
			created.setDownloadURL(extractMultipleStrings(distribution, DCAT.downloadURL));
			created.setConformsTo(extractMultipleStrings(distribution, DCTerms.conformsTo));
			created.setPages(extractMultipleDocuments(distribution, FOAF.page));
			created.setDcatSourceId(dcatSourceId);
		}
		if (dataset != null && catalog != null) {
			created.setDataset(DatasetBuilder.create(dataset, catalog, dcatSourceId));
		}

		return created;
	}
}
