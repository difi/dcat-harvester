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
import no.difi.dcat.datastore.domain.DcatSource;

public class Kibana {

	private static final String SEARCH_SOURCE_JSON = "searchSourceJson";
	private static final String KIBANA_SAVED_OBJECT_META = "kibanaSavedObjectMeta";
	private static final String ANALYZE_WILDCARD = "analyze_wildcard";
	private static final String QUERY_STRING = "queryString";
	private static final String INDEX = "index";
	private static final String QUERY = "query";
	private static final String SEARCH_TYPE = "search";
	private static final String TITLE = "title";
	private static final String INDEX_PATTERN_ID = "difi-*";
	private static final String INDEX_PATTERN_TYPE = "index-pattern";
	private static final String KIBANA_INDEX = ".kibana";
	private Elasticsearch elasticsearch = new Elasticsearch();
	private Client client;

	private final Logger logger = LoggerFactory.getLogger(Kibana.class);

	public Kibana(Client client) {
		this.client = client;
		if (elasticsearch.elasticsearchRunning(client)) {
			// Check .kibana index exists, create it if not
			if (!elasticsearch.indexExists(KIBANA_INDEX, client)) {
				elasticsearch.createIndex(KIBANA_INDEX, client);
			}
			// Check index-pattern exists for difi-*, create it if not
			if (!elasticsearch.documentExists(KIBANA_INDEX, INDEX_PATTERN_TYPE, INDEX_PATTERN_ID, client)) {
				Map<String, Object> document = new HashMap<String, Object>();
				document.put(TITLE, INDEX_PATTERN_ID);
				elasticsearch.indexDocument(KIBANA_INDEX, INDEX_PATTERN_TYPE, INDEX_PATTERN_ID, document, client);
			}
		}

	}

	public boolean delete_everything(String crawlerId) {
		// Check saved search exists, delete if so
		if (elasticsearch.documentExists(KIBANA_INDEX, SEARCH_TYPE, crawlerId, client)) {
			return (elasticsearch.deleteDocument(KIBANA_INDEX, SEARCH_TYPE, crawlerId, client));
		}
		// TODO: what else do we need to delete? everything is kinda broad.
		return true;
	}

	// TODO: Could potentially use jsonBuilder instead of maps?
	private Map<String, Object> createCrawlerSearchDocument(DcatSource dcatSource) {
		Map<String, Object> document = new HashMap<String, Object>();
		document.put(TITLE, dcatSource.getDescription());
		// Create query section
		Map<String, Object> kibanaSavedObjectMeta = new HashMap<String, Object>();
		Map<String, Object> searchSourceJson = new HashMap<String, Object>();
		Map<String, Object> query = new HashMap<String, Object>();
		Map<String, Object> queryString = new HashMap<String, Object>();
		queryString.put(QUERY, "crawler_id:\"" + dcatSource.getId() + "\"");
		queryString.put(ANALYZE_WILDCARD, true);
		query.put(QUERY_STRING, queryString);
		searchSourceJson.put(INDEX, INDEX_PATTERN_ID);
		searchSourceJson.put(QUERY, query);
		kibanaSavedObjectMeta.put(SEARCH_SOURCE_JSON, searchSourceJson);
		// End create query section
		document.put(KIBANA_SAVED_OBJECT_META, kibanaSavedObjectMeta);
		return document;
	}

	public boolean addCrawlerSearch(DcatSource dcatSource) {
		// Check saved search exists for new crawler, create it if not
		if (!elasticsearch.documentExists(KIBANA_INDEX, SEARCH_TYPE, dcatSource.getId(), client)) {
			Map<String, Object> document = createCrawlerSearchDocument(dcatSource);
			return elasticsearch.indexDocument(KIBANA_INDEX, SEARCH_TYPE, dcatSource.getId(), document, client);
		}
		return true;
	}

}
