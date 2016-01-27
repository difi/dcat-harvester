package no.difi.dcat.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.Client;

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

	public Kibana(Client client) {
		this.client = client;
		if (elasticsearch.isElasticsearchRunning(client)) {
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
	private Map<String, Object> createSearchDocument(DcatSource dcatSource) {
		Map<String, Object> source = new HashMap<String, Object>();
		source.put(TITLE, "search_crawler_operations");
		ArrayList<String> columns = new ArrayList<String>(1);
		columns.add("_source");
		source.put("columns", columns);
		ArrayList<String> sort = new ArrayList<String>(2);
		sort.add("@timestamp");
		sort.add("desc");
		source.put("sort", sort);
		// Create query section
		source.put("version", 1);
		Map<String, Object> kibanaSavedObjectMeta = new HashMap<String, Object>();
		String searchSourceJsonString = "{\"index\":\"difi-*\",\"query\":{\"query_string\":{\"query\":\"logger:crawler_operations\",\"analyze_wildcard\":true}},\"filter\":[],\"highlight\":{\"pre_tags\":[\"@kibana-highlighted-field@\"],\"post_tags\":[\"@/kibana-highlighted-field@\"],\"fields\":{\"*\":{}},\"require_field_match\":false,\"fragment_size\":2147483647}}";
		kibanaSavedObjectMeta.put(SEARCH_SOURCE_JSON, searchSourceJsonString);
		// End create query section
		source.put(KIBANA_SAVED_OBJECT_META, kibanaSavedObjectMeta);
		return source;
	}
	
	private Map<String, Object> createTablePanelDocument(DcatSource dcatSource) {
		Map<String, Object> document = new HashMap<String, Object>();
		
		
		String json = "{\"type\":\"histogram\",\"params\":{\"shareYAxis\":true,\"addTooltip\":true,\"addLegend\":true,\"scale\":\"linear\",\"mode\":\"stacked\",\"times\":[],\"addTimeMarker\":false,\"defaultYExtents\":false,\"setYExtents\":false,\"yAxis\":{}},\"aggs\":[{\"id\":\"1\",\"type\":\"count\",\"schema\":\"metric\",\"params\":{}},{\"id\":\"2\",\"type\":\"date_histogram\",\"schema\":\"segment\",\"params\":{\"field\":\"@timestamp\",\"interval\":\"auto\",\"customInterval\":\"2h\",\"min_doc_count\":1,\"extended_bounds\":{}}},{\"id\":\"3\",\"type\":\"terms\",\"schema\":\"group\",\"params\":{\"field\":\"event.raw\",\"size\":5,\"order\":\"desc\",\"orderBy\":\"1\"}}],\"listeners\":{}}";
		return null;
		
	}
	
	private Map<String, Object> createPiePanelDocument(DcatSource dcatSource) {
		Map<String, Object> document = new HashMap<String, Object>();
		document.put(TITLE, "");
		return document;
	}
	
	private Map<String, Object> createDashboardDocument(DcatSource dcatSource) {
		Map<String, Object> document = new HashMap<String, Object>();
		
		document.put(TITLE, dcatSource.getDescription());
		document.put("hits", 0);
		document.put("description", "");
		document.put("panelsJSON", "");
		
		
		Map<String, Object> kibanaSavedObjectMeta = new HashMap<String, Object>();
		Map<String, Object> searchSourceJson = new HashMap<String, Object>();
		ArrayList<Map<String, Object>> filter = new ArrayList<Map<String, Object>>();
		Map<String, Object> query = new HashMap<String, Object>();
		Map<String, Object> queryString = new HashMap<String, Object>();
		queryString.put(QUERY, "crawler_id:\"" + dcatSource.getId() + "\"");
		queryString.put(ANALYZE_WILDCARD, true);
		query.put(QUERY_STRING, queryString);
		filter.add(query);
		searchSourceJson.put("filter", filter);
		kibanaSavedObjectMeta.put(SEARCH_SOURCE_JSON, searchSourceJson);
		document.put(KIBANA_SAVED_OBJECT_META, kibanaSavedObjectMeta);
		return document;
	}
	
	public boolean addDashboardDocument(DcatSource dcatSource) {
		if(!elasticsearch.documentExists(KIBANA_INDEX, "dashboard", dcatSource.getId(), client)) {
			Map<String, Object> dashboardDocument = createDashboardDocument(dcatSource);
			return elasticsearch.indexDocument(KIBANA_INDEX, "dashboard", dcatSource.getId(), dashboardDocument, client);
		}
		return true;
	}

	public boolean addSearchDocument(DcatSource dcatSource) {
		// Check saved search exists for new crawler, create it if not
		if (!elasticsearch.documentExists(KIBANA_INDEX, SEARCH_TYPE, dcatSource.getId(), client)) {
			Map<String, Object> searchDocument = createSearchDocument(dcatSource);
			return elasticsearch.indexDocument(KIBANA_INDEX, SEARCH_TYPE, "search_crawler_operations", searchDocument, client);
		}
		return true;
	}

}
