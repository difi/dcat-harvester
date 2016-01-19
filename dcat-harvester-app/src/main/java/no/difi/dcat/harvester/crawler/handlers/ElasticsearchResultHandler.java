package no.difi.dcat.harvester.crawler.handlers;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.harvester.crawler.CrawlerResultHandler;

public class ElasticsearchResultHandler implements CrawlerResultHandler {

	private final Logger logger = LoggerFactory.getLogger(ElasticsearchResultHandler.class);
	
	@Override
	public void process(DcatSource dcatSource, Model model) {
		logger.trace("Processing results");
		// TODO Auto-generated method stub
		
	}

}
