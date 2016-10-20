package no.difi.dcat.harvester.crawler.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.difi.dcat.datastore.Elasticsearch;
import no.difi.dcat.datastore.domain.DcatSource;
import no.difi.dcat.datastore.domain.dcat.Dataset;
import no.difi.dcat.datastore.domain.dcat.Distribution;
import no.difi.dcat.datastore.domain.dcat.builders.DatasetBuilder;
import no.difi.dcat.datastore.domain.dcat.builders.DistributionBuilder;
import no.difi.dcat.harvester.crawler.CrawlerResultHandler;
import org.apache.jena.rdf.model.Model;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ElasticSearchResultHandler implements CrawlerResultHandler {

    public static String DCAT_INDEX = "dcat";
    public static String DISTRIBUTION_TYPE = "distribution";
    public static String DATASET_TYPE = "dataset";
    private final Logger logger = LoggerFactory.getLogger(ElasticSearchResultHandler.class);

    String hostename;
    int port;

    public ElasticSearchResultHandler(String hostname, int port) {
        this.hostename = hostname;
        this.port = port;
    }


    @Override
    public void process(DcatSource dcatSource, Model model) {
        logger.trace("Processing results");

        try (Elasticsearch elasticsearch = new Elasticsearch(hostename, port)) {
            indexWithElasticsearch(dcatSource, model, elasticsearch);
        } catch (Exception e) {
            throw e;
        }


    }

    protected void indexWithElasticsearch(DcatSource dcatSource, Model model, Elasticsearch elasticsearch) {
        Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create();

        if (!elasticsearch.indexExists(DCAT_INDEX)) {
            elasticsearch.createIndex(DCAT_INDEX);
        }

        BulkRequestBuilder bulkRequest = elasticsearch.getClient().prepareBulk();

        List<Distribution> distributions = new DistributionBuilder(model).build();
        logger.info("Number of distribution documents {} for dcat source {}", distributions.size(), dcatSource.getId());
        for (Distribution distribution : distributions) {

            	IndexRequest indexRequest = new IndexRequest(DCAT_INDEX,DISTRIBUTION_TYPE, distribution.getId());
            	indexRequest.source(gson.toJson(distribution));

            	logger.debug("Add distribution document {} to bulk request", distribution.getId());
            	bulkRequest.add(indexRequest);
        }

        List<Dataset> datasets = new DatasetBuilder(model).build();
        logger.info("Number of dataset documents {} for dcat source {}", datasets.size(), dcatSource.getId());
        for (Dataset dataset : datasets) {

            IndexRequest indexRequest = new IndexRequest(DCAT_INDEX,DATASET_TYPE,dataset.getId());
            indexRequest.source(gson.toJson(dataset));

            logger.debug("Add dataset document {} to bulk request", dataset.getId());
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if(bulkResponse.hasFailures()) {
            //TODO: process failures by iterating through each bulk response item?
        }
    }
    

}
