package no.difi.dcat.harvester.crawler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.DifiMeta;
import no.difi.dcat.harvester.crawler.converters.BrregAgentConverter;
import no.difi.dcat.harvester.crawler.handlers.FusekiResultHandler;
import no.difi.dcat.harvester.validation.DcatValidation;
import no.difi.dcat.harvester.validation.ValidationError;

public class CrawlerJob implements Runnable {

	private List<CrawlerResultHandler> handlers;
	private DcatSource dcatSource;
	private AdminDataStore adminDataStore;
	
	private final Logger logger = LoggerFactory.getLogger("no.difi.dcat");
	
	protected CrawlerJob(DcatSource dcatSource, AdminDataStore adminDataStore, CrawlerResultHandler... handlers) {
		this.handlers = Arrays.asList(handlers);
		this.dcatSource = dcatSource;
		this.adminDataStore = adminDataStore;
	}
	
	public String getDcatSourceId() {
		return dcatSource.getId();
	}

	@Override
	public void run() {
		logger.info("[crawler_operations] [success] Started crawler job: {}", dcatSource.toString());
		LocalDateTime start = LocalDateTime.now();


		try {
			Model union = null;
			try{

				Dataset dataset = RDFDataMgr.loadDataset(dcatSource.getUrl());

				 union = ModelFactory.createUnion(ModelFactory.createDefaultModel(), dataset.getDefaultModel());
				Iterator<String> stringIterator = dataset.listNames();

				while(stringIterator.hasNext()){
					union = ModelFactory.createUnion(union, dataset.getNamedModel(stringIterator.next()));
				}

			}catch (RiotException e){
				adminDataStore.addCrawlResults(dcatSource, DifiMeta.syntaxError, e.getMessage());
				throw e;
			}catch (HttpException e){
				adminDataStore.addCrawlResults(dcatSource, DifiMeta.networkError, e.getMessage());
				throw e;
			}
			
			BrregAgentConverter brregAgentConverter = new BrregAgentConverter();
			brregAgentConverter.collectFromModel(union);
			
			if (isValid(union)) {
				for (CrawlerResultHandler handler : handlers) {
					handler.process(dcatSource,union);
				}
			}
			
			LocalDateTime stop = LocalDateTime.now();
			logger.info("[crawler_operations] [success] Finished crawler job: {}", dcatSource.toString() + ", Duration: " + returnCrawlDuration(start, stop));
		} catch (Exception e) {
			logger.error(String.format("[crawler_operations] [fail] Error running crawler job: %1$s, error=%2$s", dcatSource.toString(), e.toString()));
		}
		

	}
	
	private boolean isValid(Model model) {
		
		final ValidationError.RuleSeverity[] status = {ValidationError.RuleSeverity.ok};
		final String[] message = {null};
		boolean validated = false;
		
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
			validated = true;
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
		
		return validated;
	}
	
	public static void main(String[] args) {
		CrawlerJob crawler = new CrawlerJob(DcatSource.getDefault(), null, new FusekiResultHandler("http://localhost:8080/fuseki/dcat", "http://localhost:8080/fuseki/admin"));
		crawler.run();
	}
	
	private String returnCrawlDuration(LocalDateTime start, LocalDateTime stop) {
		return String.valueOf(stop.compareTo(start));
	}

}
