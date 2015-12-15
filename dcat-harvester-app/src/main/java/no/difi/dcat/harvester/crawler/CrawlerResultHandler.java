package no.difi.dcat.harvester.crawler;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.harvester.service.FusekiController;

public class CrawlerResultHandler {

	private final FusekiController fuseki;
	
	private final Logger logger = LoggerFactory.getLogger(CrawlerResultHandler.class);
	
	public CrawlerResultHandler(String serviceUri) {
		this.fuseki = new FusekiController(serviceUri);
	}

	public void process(CrawlerDataSource dataSource, Model model) {
		logger.trace("Processing result from crawling");
		logger.trace("Dropping old graph {}", dataSource.getName());
		fuseki.drop(dataSource.getName());
		logger.trace("Updated new graph with data", dataSource.getName());
		fuseki.update(dataSource.getName(), model);
		logger.trace("Processing result from crawling - Done");
	}

}
