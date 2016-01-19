package no.difi.dcat.datastore;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elasticsearch {

	private static final String CLUSTER_NAME = "cluster.name";
	private String indexName;
	private String host;
	private final Logger logger = LoggerFactory.getLogger(Elasticsearch.class);

	public void Elasticsearch(String indexName, String host) {
		this.indexName = indexName;
		this.host = host;
	}
	
	// Return transport client
	public Client elasticsearchTransportClient(String host, int port, String clusterName, String nodeName) {
		// TODO: still need cluster and node names? what did I even use them for
		// in the first place?
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

	// Create data store Elasticsearch index
	public void createElasticsearchIndex(Client client, String indexName) {
		CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);
		CreateIndexResponse create = createIndexRequestBuilder.execute().actionGet();
		if(create.isAcknowledged()) {
			logger.info("Create succeeded for index: " + indexName);
		} else {
			logger.error("Create failed for index: " + indexName);
		}
	}

	// Update data store data
	public void updateElasticsearchIndex(Client client, String indexName) {
		// TODO: update meta data
		client.admin().indices().flush(new FlushRequest(indexName).waitIfOngoing(true)).actionGet();
	}

	// Delete data store Elasticsearch index
	public void deleteElasticsearchIndex(Client client, String indexName) {
		DeleteIndexResponse delete = client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
		if (delete.isAcknowledged()) {
			logger.info("Delete succeeded for index: " + indexName);
		} else {
			logger.error("Delete failed for index: " + indexName);
		}
	}

}
