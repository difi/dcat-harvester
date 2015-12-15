package no.difi.dcat.harvester.crawler;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlerJob implements Runnable {

	private CrawlerResultHandler handler;
	private CrawlerDataSource dataSource;
	
	private final Logger logger = LoggerFactory.getLogger(CrawlerJob.class);
	
	public CrawlerJob(CrawlerResultHandler handler, CrawlerDataSource dataSource) {
		this.handler = handler;
		this.dataSource = dataSource;
	}

	@Override
	public void run() {
		logger.trace("Running CrawlerJob for {}", dataSource);
		
		Model model = ModelFactory.createDefaultModel();
		model.read(dataSource.getUrl());
		
		handler.process(dataSource, model);
	}
	
	public static void main(String[] args) {
		CrawlerJob crawler = new CrawlerJob(new CrawlerResultHandler("http://localhost:8080/fuseki/dcat"), CrawlerDataSource.getDefault());
		crawler.run();
	}

}
