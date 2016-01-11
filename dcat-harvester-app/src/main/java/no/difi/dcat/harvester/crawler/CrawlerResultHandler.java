package no.difi.dcat.harvester.crawler;

import no.difi.dcat.datastore.*;
import no.difi.dcat.datastore.domain.DifiMeta;
import no.difi.dcat.harvester.validation.DcatValidation;
import no.difi.dcat.harvester.validation.ValidationError;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.DcatDataStore;
import no.difi.dcat.datastore.Fuseki;
import no.difi.dcat.datastore.domain.DcatSource;

public class CrawlerResultHandler {

	private final DcatDataStore dcatDataStore;
	private final AdminDataStore adminDataStore;

	private final Logger logger = LoggerFactory.getLogger(CrawlerResultHandler.class);

	public CrawlerResultHandler(DcatDataStore dcatDataStore, AdminDataStore adminDataStore) {
		this.dcatDataStore = dcatDataStore;
		this.adminDataStore = adminDataStore;

	}

	public CrawlerResultHandler(String serviceUriDcatDataStore, String serviceUriAdminDataStore) {
		this.dcatDataStore = new DcatDataStore(new Fuseki(serviceUriDcatDataStore));
		this.adminDataStore = new AdminDataStore(new Fuseki(serviceUriAdminDataStore));

	}


	public void process(DcatSource dcatSource, Model model) {
		logger.trace("Starting processing of results");

		final ValidationError.RuleSeverity[] status = {ValidationError.RuleSeverity.ok};
		final String[] message = {null};

		if (DcatValidation.validate(model, (error) -> {
			if (error.isError()) {
				status[0] = error.getRuleSeverity();
				message[0] = error.toString();

				logger.error(error.toString());
			}
			if (error.isWarning()) {
				if (status[0] != ValidationError.RuleSeverity.error) {
					status[0] = error.getRuleSeverity();
				}
				logger.warn(error.toString());
			} else {
				status[0] = error.getRuleSeverity();
				logger.info(error.toString());
			}
		})) {
			dcatDataStore.saveDataCatalogue(dcatSource, model);
		}

		Resource rdfStatus = null;

		switch (status[0]) {
			case error:
				rdfStatus = DifiMeta.error;
				break;
			case warning:
				rdfStatus = DifiMeta.warning;
				break;
			default:
				rdfStatus = DifiMeta.ok;
				break;
		}


		adminDataStore.addCrawlResults(dcatSource, rdfStatus, message[0]);


		logger.trace("Finished processing of results");
	}


}





