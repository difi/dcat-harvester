package no.difi.dcat.harvester.crawler;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.domain.DcatSource;

public class CrawlerJob implements Runnable {

	private CrawlerResultHandler handler;
	private DcatSource dcatSource;
	
	private final Logger logger = LoggerFactory.getLogger(CrawlerJob.class);
	
	public CrawlerJob(CrawlerResultHandler handler, DcatSource dcatSource) {
		this.handler = handler;
		this.dcatSource = dcatSource;
	}

	@Override
	public void run() {
		logger.trace("Running crawler job for {}", dcatSource.getId());
		
		Model model = ModelFactory.createDefaultModel();
		
		try {
			model.read(dcatSource.getUrl());
			handler.process(dcatSource, model);
		} catch (Exception e) {
			logger.error("Error running crawler job", e);
		}
		
		logger.trace("Finished crawler job for {}", dcatSource.getId());
	}
	
	public static void main(String[] args) {
		CrawlerJob crawler = new CrawlerJob(new CrawlerResultHandler("http://localhost:8080/fuseki/dcat", "http://localhost:8080/fuseki/admin"), DcatSource.getDefault());
		crawler.run();
	}

}
