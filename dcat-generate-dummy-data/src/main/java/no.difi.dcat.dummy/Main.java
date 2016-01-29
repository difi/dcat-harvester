package no.difi.dcat.dummy;

import no.difi.dcat.datastore.*;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.User;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import java.util.UUID;


public class Main {

	static final String EXAMPLE = "http://example.com/";
	private static final String USER = "dummyData";
	static Model defaultModel;


	public static void main(String[] args) {


		Fuseki dcat = new Fuseki("http://localhost:3030/fuseki/dcat");
		Fuseki admin = new Fuseki("http://localhost:3030/fuseki/admin");

		AdminDataStore adminDataStore = new AdminDataStore(admin);
		DcatDataStore dcatDataStore = new DcatDataStore(dcat);

		AdminDcatDataService adminDcatDataService = new AdminDcatDataService(adminDataStore, dcatDataStore);

		try {
			adminDataStore.addUser(new User(null, USER, "password", "test@example.com", "USER"));
		} catch (UserAlreadyExistsException e) {
			e.printStackTrace();
		}

		adminDataStore.getDcatSources().stream().forEach(source -> {
			adminDcatDataService.deleteDcatSource(source.getId(), new User("", "", "", "", "ADMIN"));
		});


		int numberOfCatalogs = 20;

		for (int i = 0; i < numberOfCatalogs; i++) {


			defaultModel = ModelFactory.createDefaultModel();

			for (int k = 0; k < 3; k++) {
				Catalog catalog = new Catalog();
				catalog.createAgent();
				for (int j = 0; j < 300; j++) {
					Dataset dataset1 = catalog.createDataset();
					dataset1.createDistribution();
					dataset1.createDistribution();
					dataset1.createDistribution();
					dataset1.createDistribution();
				}
			}

//			DcatValidation.validate(defaultModel, error -> {
//				if (error.isError()) {
//					System.out.println(error);
//				}
//			});

			DcatSource dcatSource = adminDataStore.addDcatSource(new DcatSource(null, "", "", USER, ""));

			dcatDataStore.saveDataCatalogue(dcatSource, defaultModel);

			System.out.println("############### " + Math.round(100.0 / (numberOfCatalogs) * i) + "%");

		}


		long size = defaultModel.size();
		System.out.println(size);

	}

	static Resource newResource() {
		return defaultModel.createResource(EXAMPLE + UUID.randomUUID().toString());
	}

	static class DCAT {
		static final String URI = "http://www.w3.org/ns/dcat#";
		static final Resource Catalog = ResourceFactory.createResource(URI + "Catalog");
		static final Resource Dataset = ResourceFactory.createResource(URI + "Dataset");
		static final Resource Distribution = ResourceFactory.createResource(URI + "Distribution");

		public static Property dataset = ResourceFactory.createProperty(URI, "dataset");
		public static Property distribution = ResourceFactory.createProperty(URI, "distribution");
		public static Property accessURL = ResourceFactory.createProperty(URI, "accessURL");
	}

	static class Agent {

		Resource agent;

		Agent() {
			agent = newResource();
			agent.addProperty(RDF.type, FOAF.Agent);
			agent.addLiteral(FOAF.name, "Name");

		}


	}

	static class Catalog {

		Resource catalog = newResource();


		Catalog() {
			catalog.addProperty(RDF.type, DCAT.Catalog);
			catalog.addLiteral(DCTerms.title, "Title");
			catalog.addLiteral(DCTerms.description, "Description");
		}

		public Dataset createDataset() {
			Dataset dataset = new Dataset();
			addDataset(dataset);
			return dataset;
		}

		public void addDataset(Dataset dataset) {
			catalog.addProperty(DCAT.dataset, dataset.dataset);
		}


		public Agent createAgent() {
			Agent agent = new Agent();
			addAgent(agent);
			return agent;

		}

		public void addAgent(Agent agent) {
			catalog.addProperty(DCTerms.publisher, agent.agent);

		}
	}

	static class Dataset {

		Resource dataset = newResource();

		Dataset() {
			dataset.addLiteral(DCTerms.title, "Title");
			dataset.addLiteral(DCTerms.description, "Description");
			dataset.addProperty(RDF.type, DCAT.Dataset);
		}


		public Distribution createDistribution() {
			Distribution distribution = new Distribution();
			dataset.addProperty(DCAT.distribution, distribution.distribution);
			return distribution;
		}
	}

	static class Distribution {

		Resource distribution = newResource();

		Distribution() {
			distribution.addProperty(RDF.type, DCAT.Distribution);
			distribution.addProperty(DCAT.accessURL, newResource());
		}

	}

}
