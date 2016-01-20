package no.difi.dcat.datastore;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.difi.dcat.datastore.Elasticsearch;

public class Kibana {

	private static final String SEARCH_TYPE = "search";
	private static final String TITLE = "title";
	private static final String INDEX_PATTERN_ID = "difi-*";
	private static final String INDEX_PATTERN_TYPE = "index-pattern";
	private static final String KIBANA_INDEX = ".kibana";
	private Client client;
	private Elasticsearch elasticsearch;

	private final Logger logger = LoggerFactory.getLogger(Kibana.class);

	public void doStuff(String crawlerId) {
		this.elasticsearch = new Elasticsearch();
		// TODO: do host and port really need to be configurable?
		this.client = elasticsearch.returnElasticsearchTransportClient("localhost", 9200);
		// Check .kibana index exists, create it if not
		if (!indexExists(KIBANA_INDEX, client)) {
			createIndex(KIBANA_INDEX, client);
		}
		// Check index-pattern exists for difi-*, create it if not
		if (!documentExists(KIBANA_INDEX, INDEX_PATTERN_TYPE, INDEX_PATTERN_ID, client)) {
			Map<String, Object> document = new HashMap<String, Object>();
			document.put(TITLE, INDEX_PATTERN_ID);
			createDocument(KIBANA_INDEX, INDEX_PATTERN_TYPE, INDEX_PATTERN_ID, document, client);
		}
		// TODO: Could potentially use this instead of a million maps?
		// import static org.elasticsearch.common.xcontent.XContentFactory.*;
		//
		// XContentBuilder builder = jsonBuilder()
		// .startObject()
		// .field("user", "kimchy")
		// .field("postDate", new Date())
		// .field("message", "trying out Elasticsearch")
		// .endObject()
		// Check saved search exists for new crawler, create it if not
		if (!documentExists(KIBANA_INDEX, SEARCH_TYPE, crawlerId, client)) {
			Map<String, Object> document = new HashMap<String, Object>();
			document.put(TITLE, crawlerId);
			// Create query section
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
			// End create query section
			document.put("kibanaSavedObjectMeta", kibanaSavedObjectMeta);
			createDocument(KIBANA_INDEX, SEARCH_TYPE, crawlerId, document, client);
		}

		// TODO: visualisations and dashboard should then just be templateable, elasticdump?

		// Always close the client when we're done with it
		client.close();
	}

	// A lot (if not all) of this could be moved into the Elasticsearch class tbh
	private void createDocument(String index, String type, String id, Map<String, Object> document, Client client) {
		IndexResponse rsp = client.prepareIndex(index, type, id).setSource(document).execute().actionGet();
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
