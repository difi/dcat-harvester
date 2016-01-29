package no.difi.dcat.harvester.crawler.handlers;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import no.difi.dcat.datastore.Elasticsearch;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.dcat.Dataset;
import no.difi.dcat.datastore.domain.dcat.Distribution;
import no.difi.dcat.datastore.domain.dcat.builders.DatasetBuilder;
import no.difi.dcat.datastore.domain.dcat.builders.DistributionBuilder;
import no.difi.dcat.harvester.crawler.CrawlerResultHandler;

public class ElasticSearchResultHandler implements CrawlerResultHandler {

	private final Logger logger = LoggerFactory.getLogger(ElasticSearchResultHandler.class);

	private Client elasticSearchClient;

	public ElasticSearchResultHandler(Client elasticSearchClient) {
		this.elasticSearchClient = elasticSearchClient;;
	}

	@Override
	public void process(DcatSource dcatSource, Model model) {
		logger.trace("Processing results");

		Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create();
		
		List<Distribution> distributions = new DistributionBuilder(model).build();
		logger.info("Number of distribution documents {} for dcat source {}", distributions.size(), dcatSource.getId());
		for (Distribution distribution : distributions) {
			String json = gson.toJson(distribution);

			logger.debug("Sending distribution document {} to ElasticSearch", distribution.getId());
			IndexResponse response = elasticSearchClient.prepareIndex("dcat", "distribution", distribution.getId()).setSource(json).execute().actionGet();
		}
		
		List<Dataset> datasets = new DatasetBuilder(model).build();
		logger.info("Number of distribution documents {} for dcat source {}", datasets.size(), dcatSource.getId());
		for (Dataset dataset : datasets) {
			String json = gson.toJson(dataset);
			
			logger.debug("Sending dataset document {} to ElasticSearch", dataset.getId());
			IndexResponse response = elasticSearchClient.prepareIndex("dcat", "dataset", dataset.getId()).setSource(json).execute().actionGet();
		}

	}
}
