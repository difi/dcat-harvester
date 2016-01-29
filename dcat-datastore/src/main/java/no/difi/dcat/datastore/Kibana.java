package no.difi.dcat.datastore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.Client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Kibana {

	private static final String _SOURCE = "_source";
	public final String CRAWLER_RESULTS_PANEL_ID = "Crawler-Results";
	public final String CRAWLER_OPERATIONS_PANEL_ID = "Crawler-Operations";
	public final String CRAWLER_METADATA_PANEL_ID = "Crawler-Metadata";
	public final String CRAWLER_OPERATIONS_SEARCH_ID = "crawler_operations";
	public final String DASHBOARD_ID = "dashboard_template";
	public final String VISUALIZATION_TYPE = "visualization";
	public final String DASHBOARD_TYPE = "dashboard";
	public final String SEARCH_TYPE = "search";
	private static final String TITLE = "title";
	public final String INDEX_PATTERN_ID = "difi-*";
	public final String INDEX_PATTERN_TYPE = "index-pattern";
	public final String KIBANA_INDEX = ".kibana";
	private Elasticsearch elasticsearch = new Elasticsearch();
	private Client client;

	public Kibana(Client client) {
		this.client = client;
		if (elasticsearch.isElasticsearchRunning(client)) {
			// Check .kibana index exists, create it if not
			if (!elasticsearch.indexExists(KIBANA_INDEX, client)) {
				elasticsearch.createIndex(KIBANA_INDEX, client);
			}
			client.admin().cluster().prepareHealth(KIBANA_INDEX).setWaitForYellowStatus().execute().actionGet();
			// Check index-pattern exists for difi-*, create it if not
			if (!elasticsearch.documentExists(KIBANA_INDEX, INDEX_PATTERN_TYPE, INDEX_PATTERN_ID, client)) {
				Map<String, Object> document = new HashMap<String, Object>();
				document.put(TITLE, INDEX_PATTERN_ID);
				elasticsearch.indexDocument(KIBANA_INDEX, INDEX_PATTERN_TYPE, INDEX_PATTERN_ID, document, client);
			}
			addSearchDocument();
			addVisualisations();
			addDashboardDocument();
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

	public void addDashboardDocument() {
		if (!elasticsearch.documentExists(KIBANA_INDEX, DASHBOARD_TYPE, DASHBOARD_ID, client)) {
			File dashboard = new File(getClass().getClassLoader().getResource("elk/dashboard.json").getFile());
			try {
				elasticsearch.indexDocument(KIBANA_INDEX, DASHBOARD_TYPE, DASHBOARD_ID,
						getElasticsearchDocument(dashboard).toString(), client);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void addSearchDocument() {
		// Check saved search exists for new crawler, create it if not
		if (!elasticsearch.documentExists(KIBANA_INDEX, SEARCH_TYPE, CRAWLER_OPERATIONS_SEARCH_ID, client)) {
			File search = new File(getClass().getClassLoader().getResource("elk/search.json").getFile());
			try {
				elasticsearch.indexDocument(KIBANA_INDEX, SEARCH_TYPE, CRAWLER_OPERATIONS_SEARCH_ID,
						getElasticsearchDocument(search).toString(), client);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addVisualisations() {
		// Check if visualisations exist, create if not
		if (!elasticsearch.documentExists(KIBANA_INDEX, VISUALIZATION_TYPE, CRAWLER_METADATA_PANEL_ID, client)) {
			File crawler_metadata = new File(getClass().getClassLoader().getResource("elk/crawler_metadata.json").getFile());
			try {
				if (elasticsearch.indexDocument(KIBANA_INDEX, VISUALIZATION_TYPE, CRAWLER_METADATA_PANEL_ID,
						getElasticsearchDocument(crawler_metadata).toString(), client)) {
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!elasticsearch.documentExists(KIBANA_INDEX, VISUALIZATION_TYPE, CRAWLER_OPERATIONS_PANEL_ID, client)) {
			File crawler_operations = new File(getClass().getClassLoader().getResource("elk/crawler_operations.json").getFile());
			try {
				elasticsearch.indexDocument(KIBANA_INDEX, VISUALIZATION_TYPE, CRAWLER_OPERATIONS_PANEL_ID,
						getElasticsearchDocument(crawler_operations).toString(), client);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!elasticsearch.documentExists(KIBANA_INDEX, VISUALIZATION_TYPE, CRAWLER_RESULTS_PANEL_ID, client)) {
			File crawler_results = new File(getClass().getClassLoader().getResource("elk/crawler_results.json").getFile());
			try {
				elasticsearch.indexDocument(KIBANA_INDEX, VISUALIZATION_TYPE, CRAWLER_RESULTS_PANEL_ID,
						getElasticsearchDocument(crawler_results).toString(), client);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private JsonObject getElasticsearchDocument(File file) throws FileNotFoundException {
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(new FileReader(file));
		JsonArray jsonArray = jsonElement.getAsJsonArray();
		JsonObject document = jsonArray.get(0).getAsJsonObject();
		JsonObject source = document.getAsJsonObject(_SOURCE);
		return source;

	}

}
