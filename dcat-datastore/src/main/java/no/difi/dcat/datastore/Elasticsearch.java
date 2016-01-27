package no.difi.dcat.datastore;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elasticsearch {

	private static final String CLUSTER_NAME = "cluster.name";
	private final Logger logger = LoggerFactory.getLogger(Elasticsearch.class);

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
		
		if(isElasticsearchRunning(client)) {
			return client;
		}
		return null;
	}

	public Client returnElasticsearchTransportClient(String host, int port) {
		Client client = null;
		try {
			client = TransportClient.builder().build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
		} catch (UnknownHostException e) {
			logger.error(e.toString());
		}
		
		if(isElasticsearchRunning(client)) {
			return client;
		}
		return null;
	}

	public boolean isElasticsearchRunning(Client client) {
		return client.admin().cluster().prepareHealth().execute().actionGet().getStatus() != null;
	}
	
	public boolean documentExists(String index, String type, String id, Client client) {
		return client.prepareGet(index, type, id).execute().actionGet().isExists();
	}
	
	public boolean indexExists(String index, Client client) {
		return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
	}
	
	public void createIndex(String index, Client client) {
		CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(index);
		CreateIndexResponse response = createIndexRequestBuilder.execute().actionGet();
		logger.info("Index " + index + " created: " + String.valueOf(response.isAcknowledged()));
	}
	
	public boolean indexDocument(String index, String type, String id, Map<String, Object> document, Client client) {
		IndexResponse rsp = client.prepareIndex(index, type, id).setSource(document).execute().actionGet();
		return rsp.isCreated();
	}

	public boolean deleteDocument(String index, String type, String id, Client client) {
		DeleteResponse rsp = client.prepareDelete(index, type, id).execute().actionGet();
		return !rsp.isFound();
	}

}
