package no.difi.dcat.harvester.crawler.handlers;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.dcat.Dataset;
import no.difi.dcat.datastore.domain.dcat.DatasetBuilder;
import no.difi.dcat.datastore.domain.dcat.Distribution;
import no.difi.dcat.datastore.domain.dcat.DistributionBuilder;
import no.difi.dcat.harvester.crawler.CrawlerResultHandler;

public class StdErrElasticsearchResultHandler implements CrawlerResultHandler {

	private final Logger logger = LoggerFactory.getLogger(StdErrElasticsearchResultHandler.class);

	@Override
	public void process(DcatSource dcatSource, Model model) {
		logger.trace("Processing results");
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		List<Distribution> distributions = new DistributionBuilder(model).build();
		for (Distribution distribution : distributions) {
			String json = gson.toJson(distribution);
			System.err.println(json);
		}
		
		List<Dataset> datasets = new DatasetBuilder(model).build();
		for (Dataset dataset : datasets) {
			String json = gson.toJson(dataset);
			System.err.println(json);
		}	
	}	
}
