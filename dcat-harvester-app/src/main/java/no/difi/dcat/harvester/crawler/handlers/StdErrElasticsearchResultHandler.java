package no.difi.dcat.harvester.crawler.handlers;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.elasticsearch.action.index.IndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.dcat.Dataset;
import no.difi.dcat.datastore.domain.dcat.Distribution;
import no.difi.dcat.datastore.domain.dcat.builders.DatasetBuilder;
import no.difi.dcat.datastore.domain.dcat.builders.DistributionBuilder;
import no.difi.dcat.harvester.ElasticSearchClient;
import no.difi.dcat.harvester.crawler.CrawlerResultHandler;

public class StdErrElasticsearchResultHandler implements CrawlerResultHandler {

	private final Logger logger = LoggerFactory.getLogger(StdErrElasticsearchResultHandler.class);

	@Autowired
	private ElasticSearchClient elasticSearchClient;
	
	@Override
	public void process(DcatSource dcatSource, Model model) {
		logger.trace("Processing results");
		
		Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create();
		
		List<Distribution> distributions = new DistributionBuilder(model).build();
		for (Distribution distribution : distributions) {
			String json = gson.toJson(distribution);
			System.err.println(json);
			
			IndexResponse response = elasticSearchClient.getClient().prepareIndex().setSource(json).get();
		}
		
		List<Dataset> datasets = new DatasetBuilder(model).build();
		for (Dataset dataset : datasets) {
			String json = gson.toJson(dataset);
			System.err.println(json);
			
			IndexResponse response = elasticSearchClient.getClient().prepareIndex().setSource(json).get();
		}
	}	
}
