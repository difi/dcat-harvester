package no.difi.dcat.datastore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.elasticsearch.client.Client;

import no.difi.dcat.datastore.domain.DcatSource;

public class Kibana {

	private static final String VISUALIZATION_TYPE = "visualization";
	private static final String DASHBOARD_TYPE = "dashboard";
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

	public boolean addDashboardDocument(DcatSource dcatSource) {
		if(!elasticsearch.documentExists(KIBANA_INDEX, DASHBOARD_TYPE, "dashboard_template", client)) {
			File dashboard = new File("elk/dashboard.json");
			try {
				return elasticsearch.indexDocument(KIBANA_INDEX, DASHBOARD_TYPE, dcatSource.getId(), getElasticsearchDocument(dashboard), client);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean addSearchDocument() {
		// Check saved search exists for new crawler, create it if not
		if (!elasticsearch.documentExists(KIBANA_INDEX, SEARCH_TYPE, "crawler_operations", client)) {
			File search = new File("elk/search.json");
			try {
				return elasticsearch.indexDocument(KIBANA_INDEX, SEARCH_TYPE, "crawler_operations", getElasticsearchDocument(search), client);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void addVisualisations() {
		// Check if visualisations exist, create if not
		boolean test = false;
		if (!elasticsearch.documentExists(KIBANA_INDEX, VISUALIZATION_TYPE, "Crawler-Metadata", client)) {
			File crawler_metadata = new File("elk/crawler_metadata.json");
			try {
				if(elasticsearch.indexDocument(KIBANA_INDEX, VISUALIZATION_TYPE, "Crawler-Metadata", getElasticsearchDocument(crawler_metadata), client)) {
					test = true;
				} 
					
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if (!elasticsearch.documentExists(KIBANA_INDEX, VISUALIZATION_TYPE, "Crawler-Operations", client)) {
			File crawler_operations = new File("elk/crawler_metadata.json");
			try {
				elasticsearch.indexDocument(KIBANA_INDEX, VISUALIZATION_TYPE, "Crawler-Operations", getElasticsearchDocument(crawler_operations), client);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if (!elasticsearch.documentExists(KIBANA_INDEX, VISUALIZATION_TYPE, "Crawler-Results", client)) {
			File crawler_metadata = new File("elk/crawler_results.json");
			try {
				elasticsearch.indexDocument(KIBANA_INDEX, VISUALIZATION_TYPE, "Crawler-Results", getElasticsearchDocument(crawler_metadata), client);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
		
	}
	
	private JsonObject getElasticsearchDocument(File file) throws FileNotFoundException {
		JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new FileReader(file));
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject source = jsonObject.getAsJsonObject("_source");
        return source;
        
		
	}

}
