package no.difi.dcat.datastore;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

public class Elasticsearch implements AutoCloseable {

	private static final String CLUSTER_NAME = "cluster.name";
	private final Logger logger = LoggerFactory.getLogger(Elasticsearch.class);

	private Client client;

	public Elasticsearch(String host, int port) {
		this.client = returnElasticsearchTransportClient(host, port, "dcatharvester");
	}

	public Elasticsearch(Client client) {
		this.client = client;
	}

	public Client returnElasticsearchTransportClient(String host, int port, String clusterName) {
		Client client = null;
		Settings settings = null;
		try {
			settings = Settings.settingsBuilder().put(CLUSTER_NAME, clusterName).build();
			client = TransportClient.builder().settings(settings).build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
		} catch (UnknownHostException e) {
			logger.error(e.toString());
		}

		return client;
	}

	public Client returnElasticsearchTransportClient(String host, int port) {
		Client client = null;
		try {
			client = TransportClient.builder().build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
		} catch (UnknownHostException e) {
			logger.error(e.toString());
		}

		return client;

	}

	public boolean isElasticsearchRunning() {
		return elasticsearchStatus() != null;
	}

	public ClusterHealthStatus elasticsearchStatus() {
		return client.admin().cluster().prepareHealth().execute().actionGet().getStatus();
	}

	public boolean documentExists(String index, String type, String id) {
		return client.prepareGet(index, type, id).execute().actionGet().isExists();
	}

	public boolean indexExists(String index) {
		return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
	}

	public void createIndex(String index) {
		client.admin().indices().prepareCreate(index).execute().actionGet();
		client.admin().cluster().prepareHealth(index).setWaitForYellowStatus().execute().actionGet();
	}

	public boolean indexDocument(String index, String type, String id, JsonObject jsonObject) {
		IndexResponse rsp = client.prepareIndex(index, type, id).setSource(jsonObject).execute().actionGet();
		return rsp.isCreated();
	}

	public boolean indexDocument(String index, String type, String id, JsonArray jsonArray) {
		IndexResponse rsp = client.prepareIndex(index, type, id).setSource(jsonArray).execute().actionGet();
		return rsp.isCreated();
	}

	public boolean indexDocument(String index, String type, String id, String string) {
		IndexResponse rsp = client.prepareIndex(index, type, id).setSource(string).execute().actionGet();
		return rsp.isCreated();
	}

	public boolean indexDocument(String index, String type, String id, Map<String, Object> map) {
		IndexResponse rsp = client.prepareIndex(index, type, id).setSource(map).execute().actionGet();
		return rsp.isCreated();
	}

	public boolean deleteDocument(String index, String type, String id) {
		DeleteResponse rsp = client.prepareDelete(index, type, id).execute().actionGet();
		return !rsp.isFound();
	}

	public void deleteAllFromSource(String index, String type, String dcatSourceID){
		try {
			logger.info("[Datastore][info]Looking for entries to delete: " + dcatSourceID + "in index" + index);

			SearchResponse rsp = client.prepareSearch(index)
					.setQuery(QueryBuilders.queryStringQuery("\"" + dcatSourceID + "\"" )
					.field("dcatSourceId"))
					.setSize(5)
					.setScroll("1m")
					.execute().actionGet();

			ArrayList<Entry<String, String>> deleteList = new ArrayList<>();
			
			while (rsp.getHits().getHits().length != 0) {
				//store _id in delete-list
				Iterator<SearchHit> hitIterator = rsp.getHits().iterator();
				while (hitIterator.hasNext()) {
					SearchHit next =  hitIterator.next();
					deleteList.add(new SimpleEntry<String, String>(next.getId(), next.getType()));
				}
				//run new query
				String scrollID = rsp.getScrollId();
				rsp = client.prepareSearchScroll(scrollID).setScroll("1m").execute().actionGet();
			}
			//deleting...
			for (Entry<String, String> entry : deleteList) {
				client.prepareDelete(index, entry.getValue(), entry.getKey()).execute();
			}

		} catch (Exception e) {
			logger.error("[datastore] [error] Something went terribly wrong!");
			logger.error(e.getMessage());
			StackTraceElement[] stackTrace = e.getStackTrace();

			for (int j = 0; j < stackTrace.length; j++) {
				logger.error(stackTrace[j].toString());
			}
		}
	}

	public void close() {
		client.close();
	}

	public Client getClient() {
		return client;
	}
}
