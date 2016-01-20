package no.difi.dcat.datastore;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.Elasticsearch;

public class Kibana {

	private static final String SEARCH_TYPE = "search";
	private static final String TITLE = "title";
	private static final String INDEX_PATTERN_ID = "difi-*";
	private static final String INDEX_PATTERN_TYPE = "index-pattern";
	private static final String KIBANA_INDEX = ".kibana";
	private Elasticsearch elasticsearch = new Elasticsearch();
	private Client client;

	private final Logger logger = LoggerFactory.getLogger(Kibana.class);

	public Kibana() {

		client = elasticsearch.returnElasticsearchTransportClient("localhost", 9300);

		if (elasticsearch.elasticsearchRunning(client)) {
			// Check .kibana index exists, create it if not
			if (!indexExists(KIBANA_INDEX, client)) {
				createIndex(KIBANA_INDEX, client);
			}
			// Check index-pattern exists for difi-*, create it if not
			if (!documentExists(KIBANA_INDEX, INDEX_PATTERN_TYPE, INDEX_PATTERN_ID, client)) {
				Map<String, Object> document = new HashMap<String, Object>();
				document.put(TITLE, INDEX_PATTERN_ID);
				indexDocument(KIBANA_INDEX, INDEX_PATTERN_TYPE, INDEX_PATTERN_ID, document, client);
			}

			// TODO: visualisations and dashboard templates

			// Always close the client when we're done with it
			client.close();
		}

	}

	public boolean delete_everything(String crawlerId) {
		// Check saved search exists, delete if so
		if (documentExists(KIBANA_INDEX, SEARCH_TYPE, crawlerId, client)) {
			return(deleteDocument(KIBANA_INDEX, SEARCH_TYPE, crawlerId, client));
		}
		// TODO: what else do we need to delete? everything is kinda broad.
		return true;
	}
	
	public boolean addCrawlerSearch(String crawlerId) {
		// Check saved search exists for new crawler, create it if not
		if (!documentExists(KIBANA_INDEX, SEARCH_TYPE, crawlerId, client)) {
			// Create saved search document for new crawler
			Map<String, Object> document = createCrawlerSearchDocument(crawlerId);
			return indexDocument(KIBANA_INDEX, SEARCH_TYPE, crawlerId, document, client);
		}
		return true;
	}

	// TODO: Could potentially use jsonBuilder instead of maps?
	private Map<String, Object> createCrawlerSearchDocument(String crawlerId) {
		Map<String, Object> document = new HashMap<String, Object>();
		document.put(TITLE, crawlerId);
		Map<String, Object> kibanaSavedObjectMeta = new HashMap<String, Object>();
		Map<String, Object> searchSourceJson = new HashMap<String, Object>();
		Map<String, Object> query = new HashMap<String, Object>();
		Map<String, Object> queryString = new HashMap<String, Object>();
		queryString.put("query", "crawler_id:\"" + crawlerId + "\"");
		queryString.put("analyze_wildcard", true);
		query.put("queryString", queryString);
		searchSourceJson.put("index", INDEX_PATTERN_ID);
		searchSourceJson.put("query", query);
		kibanaSavedObjectMeta.put("searchSourceJson", searchSourceJson);
		document.put("kibanaSavedObjectMeta", kibanaSavedObjectMeta);
		return document;
	}

	// TODO: A lot (if not all) of this could be moved into Elasticsearch
	private boolean indexDocument(String index, String type, String id, Map<String, Object> document, Client client) {
		IndexResponse rsp = client.prepareIndex(index, type, id).setSource(document).execute().actionGet();
		return rsp.isCreated();
	}

	private boolean deleteDocument(String index, String type, String id, Client client) {
		DeleteResponse rsp = client.prepareDelete(index, type, id).execute().actionGet();
		return rsp.isFound();
	}

	private boolean documentExists(String index, String type, String id, Client client) {
		return client.prepareGet(index, type, id).execute().actionGet().isExists();
	}

	private boolean indexExists(String index, Client client) {
		return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
	}

	private void createIndex(String index, Client client) {
		CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(index);
		CreateIndexResponse response = createIndexRequestBuilder.execute().actionGet();
		logger.info(".kibana index created: " + String.valueOf(response.isAcknowledged()));
	}

}
