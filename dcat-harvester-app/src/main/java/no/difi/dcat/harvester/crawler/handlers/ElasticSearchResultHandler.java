package no.difi.dcat.harvester.crawler.handlers;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.elasticsearch.action.index.IndexResponse;
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
import no.difi.dcat.harvester.ElasticSearchClient;
import no.difi.dcat.harvester.crawler.CrawlerResultHandler;

public class ElasticSearchResultHandler implements CrawlerResultHandler {

	private final Logger logger = LoggerFactory.getLogger(ElasticSearchResultHandler.class);

	private ElasticSearchClient elasticSearchClient;

	public ElasticSearchResultHandler(ElasticSearchClient elasticSearchClient) {
		this.elasticSearchClient = elasticSearchClient;
	}

	@Override
	public void process(DcatSource dcatSource, Model model) {
		logger.trace("Processing results");

		Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create();
		Elasticsearch elasticsearch = new Elasticsearch();
		// TODO: turn this into a try, catch Elasticsearch errors
		if (elasticsearch.isElasticsearchRunning(elasticSearchClient.getClient())) {
			
			logger.info("\n\n\n\n" + elasticsearch.elasticsearchStatus(elasticSearchClient.getClient()) + "\n\n\n\n");

			List<Distribution> distributions = new DistributionBuilder(model).build();
			for (Distribution distribution : distributions) {
				String json = gson.toJson(distribution);
				System.err.println(json);

				IndexResponse response = elasticSearchClient.getClient().prepareIndex("dcat", "distribution")
						.setSource(json).get();

			}

			List<Dataset> datasets = new DatasetBuilder(model).build();
			for (Dataset dataset : datasets) {
				String json = gson.toJson(dataset);
				System.err.println(json);
				IndexResponse response = elasticSearchClient.getClient().prepareIndex("dcat", "dataset").setSource(json)
						.get();

			}
		} else {
			logger.error("Unable to reach Elasticsearch");
		}

	}
}
