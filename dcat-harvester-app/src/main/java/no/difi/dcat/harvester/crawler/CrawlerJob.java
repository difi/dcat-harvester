package no.difi.dcat.harvester.crawler;

import no.difi.dcat.datastore.AdminDataStore;
import no.difi.dcat.datastore.domain.DifiMeta;

import java.time.LocalDateTime;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.domain.DcatSource;

public class CrawlerJob implements Runnable {

	private CrawlerResultHandler handler;
	private DcatSource dcatSource;
	private AdminDataStore adminDataStore;
	
	private final Logger logger = LoggerFactory.getLogger(CrawlerJob.class);
	
	public CrawlerJob(CrawlerResultHandler handler, DcatSource dcatSource, AdminDataStore adminDataStore) {
		this.handler = handler;
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
		Model model = ModelFactory.createDefaultModel();
		
		try {
			try{
				model.read(dcatSource.getUrl());
			}catch (RiotException e){
				adminDataStore.addCrawlResults(dcatSource, DifiMeta.syntaxError, e.getMessage());
				throw e;
			}catch (HttpException e){
				adminDataStore.addCrawlResults(dcatSource, DifiMeta.networkError, e.getMessage());
				throw e;
			}
			handler.process(dcatSource, model);
		} catch (Exception e) {
			logger.error("Error running crawler job", e);
		}
		
		LocalDateTime stop = LocalDateTime.now();
		logger.info("[crawler_operations] [success] Finished crawler job: {}", dcatSource.toString() + ", Duration: " + returnCrawlDuration(start, stop));
	}
	
	public static void main(String[] args) {
		CrawlerJob crawler = new CrawlerJob(new CrawlerResultHandler("http://localhost:8080/fuseki/dcat", "http://localhost:8080/fuseki/admin"), DcatSource.getDefault(), null);
		crawler.run();
	}
	
	private String returnCrawlDuration(LocalDateTime start, LocalDateTime stop) {
		return String.valueOf(stop.compareTo(start));
	}

}
